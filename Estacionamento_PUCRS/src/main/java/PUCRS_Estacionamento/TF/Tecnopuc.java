package PUCRS_Estacionamento.TF;

import java.time.LocalDateTime;
import java.time.LocalTime; // Importação necessária para o recálculo
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

    @Override
    public double getTotalGerado(GerenciadorEstacionamento ger) {
        double total = 0.0;

        for (UsoDeVaga u : historico) {
            if (u.getSaida() != null) {

                LocalDateTime entrada = u.getEntrada();
                LocalDateTime saidaRegistrada = u.getSaida();
                LocalDateTime fimDoDiaEntrada = entrada.toLocalDate().atTime(LocalTime.MAX);

                LocalDateTime limiteCalculo = saidaRegistrada;
                if (saidaRegistrada.isAfter(fimDoDiaEntrada)) {
                    limiteCalculo = fimDoDiaEntrada;
                }

                long minutosValidos = ChronoUnit.MINUTES.between(entrada, limiteCalculo);

                // Estadias < 15 minutos não são cobradas
                if (minutosValidos < 15) continue;

                double custoCorrigido = (minutosValidos / 60.0) * 1.50;

                total += custoCorrigido;
            }
        }
        return total;
    }

    public String gerarBoleto(int mes, int ano) {

        double total = 0;

        for (UsoDeVaga u : historico) {
            if (!u.isPago() && u.getSaida() != null &&
                    u.getSaida().getMonthValue() == mes && u.getSaida().getYear() == ano) {

                LocalDateTime entrada = u.getEntrada();
                LocalDateTime saidaRegistrada = u.getSaida();
                LocalDateTime fimDoDiaEntrada = entrada.toLocalDate().atTime(LocalTime.MAX);

                LocalDateTime limiteCalculo = saidaRegistrada;
                if (saidaRegistrada.isAfter(fimDoDiaEntrada)) {
                    limiteCalculo = fimDoDiaEntrada;
                }

                long minutosValidos = ChronoUnit.MINUTES.between(entrada, limiteCalculo);

                // Estadias < 15 minutos não são cobradas
                if (minutosValidos < 15) continue;

                double custoCorrigido = (minutosValidos / 60.0) * 1.50;

                total += custoCorrigido;
            }
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