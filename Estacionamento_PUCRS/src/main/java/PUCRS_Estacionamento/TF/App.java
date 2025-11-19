package PUCRS_Estacionamento.TF;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);

        GerenciadorEstacionamento ger = GerenciadorEstacionamento.getInstance();
        LocalDateTime agora = LocalDateTime.of(2025, 11, 25, 8, 0);

        String cpfEstudanteJoao = "12345678901";
        String placaJoao = "ABC1D23";

        String cpfEstudanteLucas = "78901234567";
        String placaLucas = "NOP5Q67";

        String cpfServidorCarlos = "34567890123";
        String placaCarlos = "JKL4G56";

        String cpfTecnopuc = "56789012345";
        String placaTecno1 = "STU7J89";
        String placaTecno2 = "VWX8K90";

        double receitaEsperada = 0.0;

        try {

            tentaExecutar(() -> ger.registrarEntrada(placaJoao, agora), "João Entra");

            tentaExecutar(() -> {
                ger.registrarSaida(placaJoao, agora.plusHours(4));
                validarSaldo(ger, cpfEstudanteJoao, 85.0);
            }, "Saiu (Custo R$ 15,00)");
            receitaEsperada += 15.0;

            tentaExecutar(() -> {
                ger.recarregarCredito(cpfEstudanteJoao, 15.0);
                validarSaldo(ger, cpfEstudanteJoao, 100.0);
            }, "Recarrega R$ 15,00");

            System.out.println("\nLucas");

            tentaExecutar(() -> ger.registrarEntrada(placaLucas, agora), "Entra (Saldo 0)");

            tentaExecutar(() -> {
                ger.registrarSaida(placaLucas, agora.plusHours(1));
                validarSaldo(ger, cpfEstudanteLucas, -15.0);
            }, "Sai (Saldo vira -15)");
            receitaEsperada += 15.0;

            tentaExecutar(() -> ger.registrarEntrada(placaLucas, agora.plusHours(2)), "Entra Devendo 15");

            tentaExecutar(() -> {
                ger.registrarSaida(placaLucas, agora.plusHours(3));
                validarSaldo(ger, cpfEstudanteLucas, -30.0);
            }, "Sai Novamente");
            receitaEsperada += 15.0;

            System.out.print("Bloqueio de Inadimplente");
            try {
                ger.registrarEntrada(placaLucas, agora.plusHours(4));
                System.out.println("Lucas entrou devendo 30!");
            } catch (IllegalStateException e) {
                System.out.println("Bloqueado: " + e.getMessage());
            }

            System.out.println("\nCarlos");

            tentaExecutar(() -> ger.registrarEntrada(placaCarlos, agora), "Entra");
            tentaExecutar(() -> ger.registrarSaida(placaCarlos, agora.plusHours(8)), "Sai (8 horas depois)");

            System.out.println("\nTecnopuc");

            tentaExecutar(() -> {
                ger.registrarEntrada(placaTecno1, agora);
                ger.registrarEntrada(placaTecno2, agora);
            }, "entra com 2 carros (Permitido)");

            tentaExecutar(() -> ger.registrarSaida(placaTecno1, agora.plusHours(4)), "Carro 1 Sai (4h -> R$ 6,00)");

            tentaExecutar(() -> ger.registrarSaida(placaTecno2, agora.plusMinutes(10)),
                    "Carro 2 Sai (10min -> Grátis)");

            tentaExecutar(() -> {
                String boleto = ger.gerarBoleto(cpfTecnopuc, 11, 2025);
                if (!boleto.contains("6,00") && !boleto.contains("6.00"))
                    throw new RuntimeException("Valor do boleto incorreto! Esperado 6.00. Boleto: " + boleto);
                System.out.println("   >> " + boleto);
                ger.pagarBoleto(cpfTecnopuc, 11, 2025);
            }, "Geração e Pagamento de Boleto");
            receitaEsperada += 6.00;

            System.out.println("\nalidação Contábil");

            double receitaReal = ger.relatorioReceitaMensal(11, 2025);
            System.out.println("Receita Calculada: R$ " + String.format("%.2f", receitaReal));
            System.out.println("Receita Esperada:  R$ " + String.format("%.2f", receitaEsperada));

            if (Math.abs(receitaReal - receitaEsperada) < 0.1) {
                System.out.println("A receita bateu perfeitamente!");
            } else {
                System.out.println(
                        "Diferença na receita. Verifique se a dívida do estudante entra no relatório antes de ser paga.");

                System.out.println(
                        "   (Nota: Se a diferença for R$ 30,00, é porque o sistema só conta 'Pagos' e o Lucas está devendo.");
            }

            System.out.println("\nTop 5 Clientes:");
            ger.relatorioTop5Clientes().forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void tentaExecutar(Runnable acao, String descricao) {
        System.out.print("TESTE: " + descricao + "... ");
        try {
            acao.run();
            System.out.println("OK");
        } catch (Exception e) {
            System.out.println("FALHA");
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void validarSaldo(GerenciadorEstacionamento ger, String cpf, double esperado) {
        Estudante c = (Estudante) ger.getCliente(cpf);
        if (Math.abs(c.getSaldo() - esperado) > 0.01) {
            throw new RuntimeException("Saldo incorreto! Esperado: " + esperado + ", Atual: " + c.getSaldo());
        }
    }
}