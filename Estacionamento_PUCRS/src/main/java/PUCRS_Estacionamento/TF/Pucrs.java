package PUCRS_Estacionamento.TF;

import java.time.LocalDateTime;

public class Pucrs extends Cliente {

    public Pucrs(String cpf, String nome) {
        super(cpf, nome);
    }

    @Override
    public double calcularValor(UsoDeVaga uso, LocalDateTime s) {
        return 0.0;
    }

    @Override
    public void processarSaida(UsoDeVaga uso) {
        uso.setPago(true);
    }
}