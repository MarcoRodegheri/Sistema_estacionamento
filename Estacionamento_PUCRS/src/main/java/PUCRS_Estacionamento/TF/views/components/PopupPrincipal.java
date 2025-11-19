package PUCRS_Estacionamento.TF.views.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant; 
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class PopupPrincipal extends Dialog {
    
    public interface PopupListener {
        void onCategoriaSelecionada(String categoria);
    }
    
    private PopupListener listener;
    
    public PopupPrincipal(PopupListener listener) {
        this.listener = listener;
        setHeaderTitle("Tipo de Usuário");
        setWidth("300px");
        
       
        Button btnFechar = new Button("X", e -> close());
        btnFechar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        getHeader().add(btnFechar);
        
        
        criarConteudo();
    }
    
    private void criarConteudo() {
        Button btnEstudante = new Button("Estudante", e -> {
            listener.onCategoriaSelecionada("Estudante");
            close();
        });
        
        Button btnFuncionario = new Button("Funcionário", e -> {
            listener.onCategoriaSelecionada("Funcionário");
            close();
        });
        
        Button btnTecnoPuc = new Button("Tecno Puc", e -> {
            listener.onCategoriaSelecionada("Tecno Puc");
            close();
        });
        
        estilizarBotao(btnEstudante, "#001ba6");
        estilizarBotao(btnFuncionario, "#001ba6");
        estilizarBotao(btnTecnoPuc, "#001ba6");
        
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setPadding(true);
        
        content.add(btnEstudante, btnFuncionario, btnTecnoPuc);
        add(content);
    }
    
    private void estilizarBotao(Button botao, String cor) {
        botao.getStyle()
            .set("background-color", cor)
            .set("color", "white")
            .set("border", "none")
            .set("padding", "10px 20px")
            .set("border-radius", "6px")
            .set("cursor", "pointer")
            .set("font-size", "13px")
            .set("width", "100%");
    }
}