package PUCRS_Estacionamento.TF.views.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import PUCRS_Estacionamento.TF.GerenciadorEstacionamento;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

public class TotalEntradas extends Dialog {
    
    private GerenciadorEstacionamento ger = GerenciadorEstacionamento.getInstance();
    
    private DatePicker dataInicio = new DatePicker("Data Início");
    private DatePicker dataFim = new DatePicker("Data Fim");
    private Grid<Map.Entry<String, Integer>> gridResultados = new Grid<>();
    private TextField txtTotalGeral = new TextField("Total Geral");
    
    public TotalEntradas() {
        setHeaderTitle("Relatório de Entradas");
        setWidth("450px"); 
        
       
        Button btnFechar = new Button("X", e -> close());
        btnFechar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        getHeader().add(btnFechar);
    

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);
        
        
        dataInicio.setValue(java.time.LocalDate.now().minusDays(30));
        dataFim.setValue(java.time.LocalDate.now());
        dataInicio.setWidthFull();
        dataFim.setWidthFull();
        HorizontalLayout datas = new HorizontalLayout(dataInicio, dataFim);
        datas.setWidthFull();
        
        
        Button btnConsultar = new Button("Consultar", e -> gerar());
        btnConsultar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnConsultar.getStyle().set("background-color", "#001ba6").set("color", "white");
        btnConsultar.setWidthFull();
        
        
        gridResultados.addColumn(Map.Entry::getKey).setHeader("Tipo de Usuário").setAutoWidth(true);
        gridResultados.addColumn(Map.Entry::getValue).setHeader("Qtd").setAutoWidth(true);
        gridResultados.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        gridResultados.setHeight("180px");
        
        
        txtTotalGeral.setReadOnly(true);
        txtTotalGeral.setWidthFull();

        content.add(datas, btnConsultar, gridResultados, txtTotalGeral);
        add(content);
    }
    
    private void gerar() {
        if (dataInicio.getValue() == null || dataFim.getValue() == null) return;
        
        LocalDateTime inicio = dataInicio.getValue().atStartOfDay();
        LocalDateTime fim = dataFim.getValue().atTime(LocalTime.MAX);
        
        Map<String, Integer> res = ger.relatorioEntradasPorTipo(inicio, fim);
        gridResultados.setItems(res.entrySet());
        
        int total = res.values().stream().mapToInt(Integer::intValue).sum();
        txtTotalGeral.setValue(String.valueOf(total));
    }
    
    @Override
    public void open() {
        super.open();
        gerar(); 
    }
}