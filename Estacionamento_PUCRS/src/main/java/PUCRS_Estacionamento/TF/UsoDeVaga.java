package PUCRS_Estacionamento.TF;

import java.time.LocalDateTime;

public class UsoDeVaga {
    private String placa;
    private LocalDateTime entrada;
    private LocalDateTime saida;
    private double valorPago;
    private boolean pago;
    private Vaga vaga;

    public UsoDeVaga(String placa, Vaga vaga, LocalDateTime entrada) {
        this.placa = placa;
        this.vaga = vaga;
        this.entrada = entrada;
        this.pago = false;
        this.valorPago = 0.0;
    }

    public void registrarSaida(LocalDateTime saida, double valor) {
        this.saida = saida;
        this.valorPago = valor;
        if (this.vaga != null) {
            this.vaga.liberar();
        }
    }

    public boolean isPago() {
        return pago;
    }

    public void setPago(boolean pago) {
        this.pago = pago;
    }

    public double getValorPago() {
        return valorPago;
    }

    public LocalDateTime getEntrada() {
        return entrada;
    }

    public LocalDateTime getSaida() {
        return saida;
    }

    public String getPlaca() {
        return placa;
    }

    public Vaga getVaga() {
        return vaga;
    }

    @Override
    public String toString() {

        String s = "Placa: " + placa + " | Entrada: " + entrada;
        if (saida != null) {
            s += " | Saída: " + saida + " | Valor: " + valorPago;
        } else {
            s += " | Em uso na vaga: " + (vaga != null ? vaga.getCpf() : "?");
        }
        return s;
    }
}