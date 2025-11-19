package PUCRS_Estacionamento.TF.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import PUCRS_Estacionamento.TF.*;
import PUCRS_Estacionamento.TF.views.components.*;

@Route("")
public class MainView extends VerticalLayout {

    private GerenciadorEstacionamento ger = GerenciadorEstacionamento.getInstance();

    private Grid<Cliente> gridClientes = new Grid<>(Cliente.class, false);
    private Grid<UsoDeVaga> gridVeiculos = new Grid<>(UsoDeVaga.class, false);

    public MainView() {

        setWidthFull();
        setMinHeight("100vh");
        setAlignItems(Alignment.CENTER);
        setPadding(true);
        getStyle().set("background", "linear-gradient(45deg, #ffffff 0%, #e6f7ff 100%)");

        H1 titulo = new H1("Sistema de Estacionamento - PUCRS");
        titulo.getStyle().set("color", "#001ba6").set("margin-top", "10px").set("font-size", "1.8em");

        Paragraph subtitulo = new Paragraph("CONTROLE DE ACESSO E GESTÃO");
        subtitulo.getStyle().set("color", "#666").set("font-weight", "bold").set("font-size", "0.9em");

        Button btnCadastro = new Button("NOVO CADASTRO", e -> abrirPopupPrincipal());
        estilizarBotaoGrande(btnCadastro);

        Button btnEntradaSaida = new Button("REGISTRAR ENTRADA/SAÍDA", e -> abrirPesquisa());
        estilizarBotaoGrande(btnEntradaSaida);

        Button btnTotalPorData = new Button("TOTAL DE ENTRADAS", e -> abrirTotalEntradas());
        estilizarBotaoUtil(btnTotalPorData);

        Button btnReceitaTotal = new Button("RECEITA TOTAL", e -> abrirReceitaTotal());
        estilizarBotaoUtil(btnReceitaTotal);

        Button btnGerarRelatorio = new Button("GERAR RELATÓRIO", e -> abrirGerarRelatorio());
        estilizarBotaoUtil(btnGerarRelatorio);

        Button btnOutrasConsultas = new Button("OUTRAS CONSULTAS", e -> abrirOutrasConsultas());
        estilizarBotaoUtil(btnOutrasConsultas);

        HorizontalLayout layoutBotoesSuperior = new HorizontalLayout(btnCadastro, btnEntradaSaida);
        layoutBotoesSuperior.setSpacing(true);
        layoutBotoesSuperior.setJustifyContentMode(JustifyContentMode.CENTER);

        HorizontalLayout layoutBotaoUtil = new HorizontalLayout(btnTotalPorData, btnReceitaTotal, btnGerarRelatorio,
                btnOutrasConsultas);
        layoutBotaoUtil.setSpacing(true);
        layoutBotaoUtil.setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout areaBotoes = new VerticalLayout(layoutBotoesSuperior, layoutBotaoUtil);
        areaBotoes.setAlignItems(Alignment.CENTER);
        areaBotoes.setPadding(false);

        VerticalLayout card = new VerticalLayout();
        card.setWidth("98%");
        card.setMaxWidth("1600px");
        card.setPadding(true);
        card.setSpacing(true);
        card.getStyle().set("background", "white").set("border-radius", "10px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)").set("margin-top", "20px");

        H3 tituloLog = new H3("Painel de Controle");
        tituloLog.getStyle().set("color", "#333");

        HorizontalLayout listas = new HorizontalLayout();
        listas.setWidthFull();
        listas.setSpacing(true);

        VerticalLayout c1 = new VerticalLayout();

        H4 t1 = new H4("Clientes");
        t1.getStyle().set("color", "#001ba6").set("margin-top", "0");

        gridClientes.addColumn(Cliente::getNome).setHeader("Nome").setAutoWidth(true);
        gridClientes.addColumn(c -> c.getClass().getSimpleName()).setHeader("Tipo").setAutoWidth(true);
        gridClientes.addColumn(c -> c.getVeiculos().stream().map(Veiculo::getPlaca).collect(Collectors.joining(", ")))
                .setHeader("Placas").setAutoWidth(true);

        gridClientes.addComponentColumn(cliente -> {
            Button btnFin = new Button("$");
            btnFin.addClickListener(e -> abrirFinanceiro(cliente));
            btnFin.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_SMALL);
            return btnFin;
        }).setHeader("Financeiro").setAutoWidth(true);

        gridClientes.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        gridClientes.getStyle().set("font-size", "12px");
        gridClientes.setHeight("300px");

        gridClientes.addItemClickListener(event -> {
            if (event.getItem() != null)
                abrirPopupEdicao(event.getItem());
        });

        c1.add(t1, gridClientes);

        VerticalLayout c2 = new VerticalLayout();

        H4 t2 = new H4("Estacionamento");
        t2.getStyle().set("color", "#d32f2f").set("margin-top", "0");

        gridVeiculos.addColumn(u -> {
            return ger.getTodosClientes().stream().filter(c -> c.possuiVeiculo(u.getPlaca())).findFirst().orElse(null)
                    .getNome();
        }).setHeader("Cliente").setAutoWidth(true);

        gridVeiculos.addColumn(UsoDeVaga::getPlaca).setHeader("Placa");
        gridVeiculos.addColumn(u -> u.getEntrada().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")))
                .setHeader("Chegada");

        gridVeiculos.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        gridVeiculos.getStyle().set("font-size", "12px");
        gridVeiculos.setHeight("300px");
        c2.add(t2, gridVeiculos);

        listas.add(c1, c2);
        c1.setWidth("60%");
        c2.setWidth("40%");

        card.add(tituloLog, new Hr(), listas);
        add(titulo, subtitulo, areaBotoes, card);

        atualizarLogs();
    }

    private void abrirPopupPrincipal() {
        new PopupPrincipal(this::abrirPopupSecundario).open();
    }

    private void abrirPopupSecundario(String t) {
        new PopupSecundario(t, this::salvarCliente, this::abrirPopupPrincipal).open();
    }

    private void abrirPopupEdicao(Cliente c) {
        new PopupSecundario(c, this::salvarCliente).open();
    }

    private void abrirTotalEntradas() {
        new TotalEntradas().open();
    }

    private void abrirReceitaTotal() {
        new ReceitaTotal().open();
    }

    private void abrirGerarRelatorio() {
        new GerarRelatorio().open();
    }

    private void abrirOutrasConsultas() {
        new OutrasConsultas().open();
    }

    private void abrirFinanceiro(Cliente cliente) {
        Dialog janela = new Dialog();
        janela.setHeaderTitle("Financeiro: " + cliente.getNome());

        Button btnFechar = new Button("X", e -> janela.close());
        btnFechar.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY);
        janela.getHeader().add(btnFechar);

        VerticalLayout layout = new VerticalLayout();

        if (cliente instanceof Estudante) {
            Estudante est = (Estudante) cliente;

            Span textoSaldo = new Span("Saldo: R$ " + String.format("%.2f", est.getSaldo()));

            com.vaadin.flow.component.combobox.ComboBox<Double> combo = new com.vaadin.flow.component.combobox.ComboBox<>(
                    "Recarga");
            combo.setItems(15.0, 50.0, 100.0, 150.0);
            combo.setWidthFull();

            Button btnRecarregar = new Button("Confirmar Recarga", ev -> {
                if (combo.getValue() != null) {
                    ger.recarregarCredito(est.getCpf(), combo.getValue());
                    Notification.show("Recarga realizada!", 3000, Notification.Position.BOTTOM_START)
                            .addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_SUCCESS);
                    janela.close();
                }
            });
            btnRecarregar.setWidthFull();
            btnRecarregar.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY);

            layout.add(textoSaldo, combo, btnRecarregar);

        } else if (cliente instanceof Tecnopuc) {
            Tecnopuc tec = (Tecnopuc) cliente;
            int anoAtual = LocalDateTime.now().getYear();

            com.vaadin.flow.component.combobox.ComboBox<Integer> comboMes = new com.vaadin.flow.component.combobox.ComboBox<>(
                    "Mês de Referência");
            comboMes.setItems(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
            comboMes.setValue(LocalDateTime.now().getMonthValue());
            comboMes.setWidthFull();

            TextArea areaTexto = new TextArea("Detalhes do Boleto");
            areaTexto.setReadOnly(true);
            areaTexto.setWidthFull();
            areaTexto.setHeight("120px");

            comboMes.addValueChangeListener(event -> {
                if (event.getValue() != null) {
                    areaTexto.setValue(ger.gerarBoleto(tec.getCpf(), event.getValue(), anoAtual));
                }
            });

            areaTexto.setValue(ger.gerarBoleto(tec.getCpf(), comboMes.getValue(), anoAtual));

            Button btnPagar = new Button("Pagar Boleto Selecionado", ev -> {
                if (comboMes.getValue() != null) {
                    ger.pagarBoleto(tec.getCpf(), comboMes.getValue(), anoAtual);
                    Notification
                            .show("Boleto do mês " + comboMes.getValue() + " pago com sucesso!", 3000,
                                    Notification.Position.BOTTOM_START)
                            .addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_SUCCESS);
                    janela.close();
                }
            });
            btnPagar.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY);
            btnPagar.setWidthFull();

            layout.add(comboMes, areaTexto, btnPagar);

        } else {
            layout.add(new Span("Este tipo de usuário é isento de pagamento."));
        }
        janela.add(layout);
        janela.open();
    }

    private void salvarCliente(Cliente c) {
        try {
            boolean jaExiste = (ger.getCliente(c.getCpf()) != null);
            if (!jaExiste)
                ger.cadastrarCliente(c);
            Notification.show("Salvo: " + c.getNome(), 3000, Notification.Position.BOTTOM_START);
            atualizarLogs();
        } catch (Exception e) {
            Notification.show("Erro: " + e.getMessage(), 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_ERROR);
        }
    }

    private void abrirPesquisa() {
        Dialog d = new Dialog();
        d.setHeaderTitle("Controle Manual (Portaria)");

        Button btnFechar = new Button("X", e -> d.close());
        btnFechar.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY);
        d.getHeader().add(btnFechar);

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        TextField tPlaca = new TextField("Placa do Veículo");
        tPlaca.setPlaceholder("XXX-0000");
        tPlaca.setAutofocus(true);
        tPlaca.setWidthFull();
        com.vaadin.flow.component.datetimepicker.DateTimePicker pickerEntrada = new com.vaadin.flow.component.datetimepicker.DateTimePicker(
                "Data/Hora Entrada");
        pickerEntrada.setValue(LocalDateTime.now().minusHours(1));
        pickerEntrada.setWidthFull();
        com.vaadin.flow.component.datetimepicker.DateTimePicker pickerSaida = new com.vaadin.flow.component.datetimepicker.DateTimePicker(
                "Data/Hora Saída (Apenas p/ Saída)");
        pickerSaida.setValue(LocalDateTime.now());
        pickerSaida.setWidthFull();

        Button bEntrada = new Button("Registrar Entrada", e -> {
            try {
                String placa = tPlaca.getValue().trim().toUpperCase();
                ger.registrarEntrada(placa, pickerEntrada.getValue());

                String mensagem = "Entrada Registrada com Sucesso!";
                boolean isAviso = false;
                Cliente c = ger.getTodosClientes().stream().filter(cli -> cli.possuiVeiculo(placa)).findFirst()
                        .orElse(null);
                if (c instanceof Estudante) {
                    double saldo = ((Estudante) c).getSaldo();
                    if (saldo < 0) {
                        mensagem = "ATENÇÃO: Entrada liberada, mas saldo devedor de R$ " + String.format("%.2f", saldo);
                        isAviso = true;
                    }
                }
                Notification notif = Notification.show(mensagem, 5000, Notification.Position.BOTTOM_START);
                if (isAviso)
                    notif.addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_ERROR);
                else
                    notif.addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_SUCCESS);

                atualizarLogs();
                d.close();
            } catch (Exception ex) {
                Notification.show("Erro: " + ex.getMessage(), 5000, Notification.Position.BOTTOM_START)
                        .addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_ERROR);
            }
        });
        bEntrada.getThemeNames().add("primary success");
        bEntrada.setWidthFull();

        Button bSaida = new Button("Registrar Saída", e -> {
            try {
                String placa = tPlaca.getValue().trim().toUpperCase();
                Cliente cliente = ger.getTodosClientes().stream().filter(c -> c.possuiVeiculo(placa)).findFirst()
                        .orElse(null);

                ger.registrarSaida(placa, pickerSaida.getValue());

                double valorMostrado = 0.0;
                if (cliente != null && !cliente.getHistorico().isEmpty()) {
                    valorMostrado = cliente.getHistorico().get(cliente.getHistorico().size() - 1).getValorPago();
                }

                String msg = "Saída Registrada! Valor: R$ " + String.format("%.2f", valorMostrado);
                Notification notif = Notification.show(msg, 5000, Notification.Position.BOTTOM_START);

                if (valorMostrado > 0)
                    notif.addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_SUCCESS);
                else
                    notif.addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_PRIMARY);

                atualizarLogs();
                d.close();
            } catch (Exception ex) {
                Notification.show("Erro: " + ex.getMessage(), 5000, Notification.Position.BOTTOM_START)
                        .addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_ERROR);
            }
        });
        bSaida.getThemeNames().add("primary error");
        bSaida.setWidthFull();

        layout.add(tPlaca, pickerEntrada, bEntrada, new Hr(), pickerSaida, bSaida);
        d.add(layout);
        d.open();
    }

    private void atualizarLogs() {
        gridClientes.setItems(ger.getTodosClientes());
        gridVeiculos.setItems(ger.getVeiculosNoPatio());
        gridClientes.getDataProvider().refreshAll();
    }

    private void estilizarBotaoGrande(Button btn) {
        btn.getStyle().set("background", "#001ba6").set("color", "white").set("padding", "15px 25px")
                .set("font-size", "14px").set("font-weight", "bold").set("border-radius", "8px")
                .set("cursor", "pointer");
        btn.setWidth("350px");
    }

    private void estilizarBotaoUtil(Button btn) {
        btn.getStyle().set("margin", "5px").set("background", "white").set("border", "2px solid #001ba6")
                .set("border-radius", "10px").set("padding", "15px").set("color", "#001ba6").set("font-weight", "bold");
        btn.setWidth("220px");
    }
}