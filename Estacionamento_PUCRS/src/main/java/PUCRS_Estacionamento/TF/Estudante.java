package PUCRS_Estacionamento.TF;

import java.time.LocalDateTime;

public class Estudante extends Cliente {

    private double saldo;

    public Estudante(String cpf, String nome) {
        super(cpf, nome);
        this.saldo = 0.0;
    }

    public void recarregar(double valor) {
        if (valor == 15.0 || valor == 50.0 || valor == 100.0 || valor == 150.0) {
            this.saldo += valor;
        } else {

            throw new IllegalArgumentException("Valor inválido para recarga.");
        }
    }

    public double getSaldo() {
        return saldo;
    }

    @Override
    public double calcularValor(UsoDeVaga uso, LocalDateTime saida) {
        return 15.0;
    }

    @Override
    public void processarSaida(UsoDeVaga uso) {
        this.saldo = this.saldo - uso.getValorPago();
        uso.setPago(true);
    }
}