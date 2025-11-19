package PUCRS_Estacionamento.TF.views.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import PUCRS_Estacionamento.TF.GerenciadorEstacionamento;
import java.text.NumberFormat;
import java.util.Locale;

public class OutrasConsultas extends Dialog {
    
    private GerenciadorEstacionamento ger = GerenciadorEstacionamento.getInstance();
    
    private TextField txtValorMedio = new TextField("Preço Médio por Uso"); 
    private Grid<String> gridTop5 = new Grid<>();

    public OutrasConsultas() {
        setHeaderTitle("Consultas Especiais");
        setWidth("450px"); 
        
        
        Button btnFechar = new Button("X", e -> close());
        btnFechar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        getHeader().add(btnFechar);

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);
        
        
        H3 tituloMedio = new H3("Preço Médio"); 
        
        txtValorMedio.setReadOnly(true);
        txtValorMedio.setWidthFull();
        txtValorMedio.setLabel("Valor Médio de Todas as Estadias Pagas"); // Rótulo completo
        
        H3 tituloTop5 = new H3("Top 5 Clientes (Maior Receita)");
        configurarGridTop5();
        
        
        content.add(tituloMedio, txtValorMedio, tituloTop5, gridTop5);
        add(content);
        
        carregarDados();
    }
    
    private void configurarGridTop5() {
        gridTop5.addColumn(item -> item)
            .setHeader("Cliente - Valor Gerado")
            .setAutoWidth(true)
            .setFlexGrow(1);
            
        gridTop5.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        gridTop5.setHeight("200px");
        gridTop5.setWidthFull();
    }

    private void carregarDados() {
        try {
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

            
            double valorMedio = ger.consultarValorMedioPorUso(); 
            txtValorMedio.setValue(format.format(valorMedio));
            
          
            gridTop5.setItems(ger.relatorioTop5Clientes());
            
        } catch (Exception e) {
             Notification.show("Erro ao calcular métricas: " + e.getMessage(), 5000, Notification.Position.BOTTOM_START);
        }
    }
    
    @Override
    public void open() {
        super.open();
    }
}