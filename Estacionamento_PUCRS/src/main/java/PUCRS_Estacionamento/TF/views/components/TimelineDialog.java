package PUCRS_Estacionamento.TF.views.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import PUCRS_Estacionamento.TF.GerenciadorEstacionamento;
import PUCRS_Estacionamento.TF.UsoDeVaga;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Simples diálogo que mostra um "timeline" de eventos (entradas/saídas)
 */
public class TimelineDialog extends Dialog {

    private GerenciadorEstacionamento ger = GerenciadorEstacionamento.getInstance();
    private VerticalLayout container = new VerticalLayout();

    public TimelineDialog() {
        setHeaderTitle("📅 Timeline de Eventos");
        setWidth("800px");
        setHeight("600px");

        container.setPadding(true);
        container.setSpacing(true);

        Button btnRefresh = new Button("Atualizar", e -> carregarTimeline());
        btnRefresh.getStyle().set("background-color", "#001ba6").set("color", "white");

        add(new HorizontalLayout(btnRefresh));
        add(container);

        carregarTimeline();
    }

    private void carregarTimeline() {
        container.removeAll();

        List<TimelineItem> items = new ArrayList<>();

        // 1) Eventos de entrada para veículos atualmente no pátio
        for (UsoDeVaga u : ger.getVeiculosNoPatio()) {
            items.add(new TimelineItem(u.getEntrada(), "ENTRADA: " + u.getPlaca() + " - Vaga: " + (u.getVaga() != null ? u.getVaga().getCpf() : "?")));
        }

        // 2) Eventos históricos (saídas) de todos os clientes
        for (var c : ger.getTodosClientes()) {
            for (UsoDeVaga u : c.getHistorico()) {
                if (u.getSaida() != null) {
                    String msg = "SAÍDA: " + u.getPlaca() + " - Valor: R$ " + String.format("%.2f", u.getValorPago());
                    items.add(new TimelineItem(u.getSaida(), msg));
                }
            }
        }

        // Ordena por data (desc)
        items.sort(Comparator.comparing(TimelineItem::getData).reversed());

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (TimelineItem it : items) {
            HorizontalLayout row = new HorizontalLayout();
            row.setWidthFull();
            row.getStyle().set("align-items", "center");

            Span ts = new Span(it.getData().format(df));
            ts.getStyle().set("color", "#555").set("min-width", "180px");

            Div message = new Div(new Span(it.getMensagem()));
            message.getStyle().set("padding", "8px").set("background-color", "#f7f7f7").set("border-radius", "6px");

            row.add(ts, message);
            container.add(row);
        }

        if (items.isEmpty()) container.add(new H3("Sem eventos para exibir."));
    }

    private static class TimelineItem {
        private LocalDateTime data;
        private String mensagem;

        public TimelineItem(LocalDateTime data, String mensagem) {
            this.data = data;
            this.mensagem = mensagem;
        }

        public LocalDateTime getData() { return data; }
        public String getMensagem() { return mensagem; }
    }
}
