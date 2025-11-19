package PUCRS_Estacionamento.TF.views.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import PUCRS_Estacionamento.TF.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GerarRelatorio extends Dialog {
    
    private GerenciadorEstacionamento ger = GerenciadorEstacionamento.getInstance();

    public GerarRelatorio() {
        setHeaderTitle("Relatório de Uso por Cliente");
        setWidth("500px");
        
        Button btnFechar = new Button("X", e -> close());
        btnFechar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        getHeader().add(btnFechar);

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);
        
        ComboBox<Cliente> comboCliente = new ComboBox<>("Selecione o Cliente");
        comboCliente.setItems(ger.getTodosClientes());
        comboCliente.setItemLabelGenerator(c -> c.getNome() + " (" + c.getClass().getSimpleName() + ")");
        comboCliente.setWidthFull();
        
        DatePicker dataInicio = new DatePicker("Data Início");
        dataInicio.setValue(java.time.LocalDate.now().minusDays(30));
        
        DatePicker dataFim = new DatePicker("Data Fim");
        dataFim.setValue(java.time.LocalDate.now());
        
        HorizontalLayout datas = new HorizontalLayout(dataInicio, dataFim);
        datas.setWidthFull();

        TextArea areaResultado = new TextArea("Resumo do Relatório");
        areaResultado.setReadOnly(true);
        areaResultado.setWidthFull();
        areaResultado.setHeight("200px");

        Button btnGerar = new Button("Gerar Relatório", e -> {
            Cliente c = comboCliente.getValue();
            
            if (c == null || dataInicio.getValue() == null || dataFim.getValue() == null) {
                areaResultado.setValue("Selecione cliente e datas.");
                return;
            }

            LocalDateTime inicio = dataInicio.getValue().atStartOfDay();
            LocalDateTime fim = dataFim.getValue().atTime(LocalTime.MAX);

            List<UsoDeVaga> todosUsos = new ArrayList<>(c.getHistorico());
            List<UsoDeVaga> ativos = ger.getVeiculosNoPatio().stream()
                .filter(u -> c.possuiVeiculo(u.getPlaca()))
                .collect(Collectors.toList());
            todosUsos.addAll(ativos);

            List<UsoDeVaga> usosNoPeriodo = todosUsos.stream()
                .filter(u -> u.getEntrada().isAfter(inicio) && u.getEntrada().isBefore(fim))
                .collect(Collectors.toList());

            if (usosNoPeriodo.isEmpty()) {
                areaResultado.setValue("Nenhum registro encontrado.");
                return;
            }

            
            int qtdTotal = usosNoPeriodo.size();
            double custoTotal = 0.0;
            long totalMinutos = 0;
            
            for (UsoDeVaga u : usosNoPeriodo) {
                LocalDateTime dataFimCalculo;
                
                if (u.getSaida() != null) {
                    
                    dataFimCalculo = u.getSaida();
                    custoTotal += u.getValorPago();
                } else {
                    
                    dataFimCalculo = LocalDateTime.now();
                    
                    custoTotal += c.calcularValor(u, dataFimCalculo);
                }
                
                totalMinutos += ChronoUnit.MINUTES.between(u.getEntrada(), dataFimCalculo);
            }
            
            long horas = totalMinutos / 60;
            long minutos = totalMinutos % 60;
            String tempoFormatado = String.format("%d horas e %d minutos", horas, minutos);
            String ultimoCarro = usosNoPeriodo.get(usosNoPeriodo.size() - 1).getPlaca();

            StringBuilder sb = new StringBuilder();
            sb.append("=== RELATÓRIO DE USO ===\n");
            sb.append("Cliente: ").append(c.getNome()).append("\n");
            sb.append("Tipo: ").append(c.getClass().getSimpleName()).append("\n");
            sb.append("Período: ").append(dataInicio.getValue()).append(" até ").append(dataFim.getValue()).append("\n\n");
            
            sb.append("📊 ESTATÍSTICAS:\n");
            sb.append("• Total de Acessos: ").append(qtdTotal).append("\n");
            sb.append("• Tempo Total: ").append(tempoFormatado).append("\n");
            sb.append("• Custo Total: R$ ").append(String.format("%.2f", custoTotal)).append("\n");
            sb.append("• Último Veículo: ").append(ultimoCarro).append("\n");

            areaResultado.setValue(sb.toString());
        });
        
        btnGerar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnGerar.getStyle().set("background-color", "#001ba6").set("color", "white");
        btnGerar.setWidthFull();

        content.add(comboCliente, datas, btnGerar, areaResultado);
        add(content);
    }
    
    @Override
    public void open() { super.open(); }
}