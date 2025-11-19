package PUCRS_Estacionamento.TF;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class Tecnopuc extends Cliente {

    public Tecnopuc(String cpf, String nome) {
        super(cpf, nome);
    }

    @Override
    public double calcularValor(UsoDeVaga uso, LocalDateTime saida) {
        long minutos = ChronoUnit.MINUTES.between(uso.getEntrada(), saida);
        return (minutos / 60.0) * 1.50;
    }

    @Override
    public void processarSaida(UsoDeVaga uso) {
        uso.setPago(false);
    }

    public String gerarBoleto(int mes, int ano) {
        List<UsoDeVaga> pendentes = historico.stream()
                .filter(u -> !u.isPago())
                .filter(u -> u.getSaida() != null && u.getSaida().getMonthValue() == mes
                        && u.getSaida().getYear() == ano)
                .collect(Collectors.toList());

        double total = 0;
        for (UsoDeVaga u : pendentes) {
            total += u.getValorPago();
        }

        return "Empresa: " + getNome() + " | Mês: " + mes + " | Total a Pagar: R$ " + String.format("%.2f", total);
    }

    public void pagarBoleto(int mes, int ano) {
        for (UsoDeVaga uso : historico) {
            if (!uso.isPago() && uso.getSaida() != null) {
                if (uso.getSaida().getMonthValue() == mes && uso.getSaida().getYear() == ano) {
                    uso.setPago(true);
                }
            }
        }
    }
}