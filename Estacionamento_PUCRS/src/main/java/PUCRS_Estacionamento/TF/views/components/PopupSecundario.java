package PUCRS_Estacionamento.TF.views.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant; 
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import PUCRS_Estacionamento.TF.*;
import java.util.ArrayList;
import java.util.List;

public class PopupSecundario extends Dialog {
    
    public interface FinalizarListener {
        void onFinalizarCadastro(Cliente cliente);
    }
    
    private FinalizarListener listener;
    private String tipoUsuario;
    private Runnable voltarListener;
    private Cliente clienteEmEdicao;

    
    private TextField txtNome = new TextField("Nome Completo");
    private TextField txtCpf = new TextField("CPF");
    
    
    private VerticalLayout containerPlacas = new VerticalLayout();
    private Button btnAddPlaca = new Button("Adicionar outra placa (+)");

    
    public PopupSecundario(String tipoUsuario, FinalizarListener listener, Runnable voltarAction) {
        this.tipoUsuario = tipoUsuario;
        this.listener = listener;
        this.voltarListener = voltarAction;
        this.clienteEmEdicao = null;
        
        setHeaderTitle("Novo Cadastro - " + tipoUsuario);
        
        
        adicionarBotaoFechar();
       
        
        adicionarCampoPlaca(""); 
        montarLayout();
    }

    
    public PopupSecundario(Cliente clienteParaEditar, FinalizarListener listener) {
        this.clienteEmEdicao = clienteParaEditar;
        this.listener = listener;
        
        if (clienteParaEditar instanceof Estudante) this.tipoUsuario = "Estudante";
        else if (clienteParaEditar instanceof Tecnopuc) this.tipoUsuario = "Tecno Puc";
        else this.tipoUsuario = "Funcionário";

        setHeaderTitle("Editar - " + tipoUsuario);
        
       
        adicionarBotaoFechar();
       
        
        txtNome.setValue(clienteParaEditar.getNome());
        txtCpf.setValue(clienteParaEditar.getCpf());
        txtCpf.setReadOnly(true);
        
        List<Veiculo> veiculos = clienteParaEditar.getVeiculos();
        if (veiculos.isEmpty()) {
            adicionarCampoPlaca(""); 
        } else {
            for (Veiculo v : veiculos) {
                adicionarCampoPlaca(v.getPlaca());
            }
        }

        montarLayout();
    }
    
   
    private void adicionarBotaoFechar() {
        Button btnFechar = new Button("X", e -> close());
        btnFechar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        getHeader().add(btnFechar);
    }
    
    private void montarLayout() {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(false);
        content.setPadding(true);
        setWidth("350px");

        txtNome.setWidthFull();
        txtCpf.setWidthFull();
        
        containerPlacas.setPadding(false);
        containerPlacas.setSpacing(true);
        containerPlacas.setWidthFull();
        
        btnAddPlaca.addClickListener(e -> adicionarCampoPlaca(""));
        btnAddPlaca.getStyle().set("font-size", "12px").set("color", "#001ba6").set("cursor", "pointer");
        btnAddPlaca.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button btnSalvar = new Button(clienteEmEdicao == null ? "Salvar" : "Atualizar");
        btnSalvar.addClickListener(e -> salvar());
        estilizarBotaoFinalizar(btnSalvar);

        content.add(txtNome, txtCpf, new H5("Veículos Cadastrados"), containerPlacas, btnAddPlaca, btnSalvar);
        
        if (clienteEmEdicao == null && voltarListener != null) {
            Button btnVoltar = new Button("Voltar", e -> { close(); voltarListener.run(); });
            estilizarBotao(btnVoltar);
            content.add(btnVoltar);
        }
        
        verificarLimitePlacas();
        add(content);
    }

    private void adicionarCampoPlaca(String valorInicial) {
        int numero = containerPlacas.getComponentCount() + 1;
        TextField txtPlaca = new TextField("Placa " + numero); 
        
        txtPlaca.setPlaceholder("AAA-0000");
        txtPlaca.setValue(valorInicial);
        txtPlaca.setWidthFull();
        
        containerPlacas.add(txtPlaca);
        verificarLimitePlacas();
    }
    
    private void verificarLimitePlacas() {
        int qtdAtual = containerPlacas.getComponentCount();
        if (tipoUsuario.equals("Tecno Puc")) {
            btnAddPlaca.setVisible(true);
        } else {
            if (qtdAtual >= 2) btnAddPlaca.setVisible(false);
            else btnAddPlaca.setVisible(true);
        }
    }

    private void salvar() {
        try {
            if (txtNome.getValue().isEmpty() || txtCpf.getValue().isEmpty()) {
                Notification.show("Preencha nome e CPF!"); return;
            }

            Cliente tempCliente;

            if (clienteEmEdicao == null) {
                if (tipoUsuario.equals("Estudante")) {
                    tempCliente = new Estudante(txtCpf.getValue(), txtNome.getValue());
                } else if (tipoUsuario.equals("Tecno Puc")) {
                    tempCliente = new Tecnopuc(txtCpf.getValue(), txtNome.getValue());
                } else {
                    tempCliente = new Pucrs(txtCpf.getValue(), txtNome.getValue());
                }
            } else {
                tempCliente = clienteEmEdicao;
                List<Veiculo> atuais = new ArrayList<>(tempCliente.getVeiculos());
                for (Veiculo v : atuais) {
                    tempCliente.removerVeiculo(v.getPlaca());
                }
            }
            
            final Cliente clienteFinal = tempCliente;
            
            containerPlacas.getChildren().forEach(component -> {
                if (component instanceof TextField) {
                    String placaDigitada = ((TextField) component).getValue().trim().toUpperCase();
                    if (!placaDigitada.isEmpty()) {
                        clienteFinal.addVeiculo(new Veiculo(placaDigitada));
                    }
                }
            });

            listener.onFinalizarCadastro(clienteFinal);
            close();

        } catch (Exception ex) {
            Notification.show("Erro ao salvar: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void estilizarBotaoFinalizar(Button btn) {
        btn.getStyle().set("background", "#27AE60").set("color", "white").set("width", "100%").set("margin-top", "10px");
    }
    private void estilizarBotao(Button btn) {
        btn.getStyle().set("background", "#95a5a6").set("color", "white").set("width", "100%").set("margin-top", "5px");
    }
}