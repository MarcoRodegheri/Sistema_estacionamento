package PUCRS_Estacionamento.TF.views.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import PUCRS_Estacionamento.TF.GerenciadorEstacionamento;
import java.time.LocalDate;

public class ReceitaTotal extends Dialog {
    
    private GerenciadorEstacionamento ger = GerenciadorEstacionamento.getInstance();
    
    private ComboBox<Integer> comboMes = new ComboBox<>("Mês");
    private ComboBox<Integer> comboAno = new ComboBox<>("Ano");
    private TextField txtReceita = new TextField("Receita Total");

    public ReceitaTotal() {
        setHeaderTitle("Relatório de Receita");
        setWidth("400px");
        
        
        Button btnFechar = new Button("X", e -> close());
        btnFechar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        getHeader().add(btnFechar);

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);
        
       
        comboMes.setItems(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        comboMes.setValue(LocalDate.now().getMonthValue());
        comboMes.setWidth("50%");
        
        comboAno.setItems(2023, 2024, 2025, 2026);
        comboAno.setValue(LocalDate.now().getYear());
        comboAno.setWidth("50%");
        
        HorizontalLayout linhaData = new HorizontalLayout(comboMes, comboAno);
        linhaData.setWidthFull();
        
        
        Button btnCalcular = new Button("Calcular Receita", e -> calcular());
        btnCalcular.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnCalcular.getStyle().set("background-color", "#001ba6").set("color", "white");
        btnCalcular.setWidthFull();
        
        
        txtReceita.setReadOnly(true);
        txtReceita.setWidthFull();

        content.add(linhaData, btnCalcular, txtReceita);
        add(content);
    }
    
    private void calcular() {
        if (comboMes.getValue() == null || comboAno.getValue() == null) return;
        
        double valor = ger.relatorioReceitaMensal(comboMes.getValue(), comboAno.getValue());
        txtReceita.setValue(String.format("R$ %.2f", valor));
    }
    
    @Override
    public void open() {
        super.open();
        calcular(); 
    }
}