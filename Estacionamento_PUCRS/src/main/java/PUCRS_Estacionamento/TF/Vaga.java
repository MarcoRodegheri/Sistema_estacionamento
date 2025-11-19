package PUCRS_Estacionamento.TF;

import java.util.Objects;

public class Vaga {
    private String cpf;
    private boolean disponivel;

    public Vaga(String cpf) {
        this.cpf = cpf;
        this.disponivel = true;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void ocupar() {
        this.disponivel = false;
    }

    public void liberar() {
        this.disponivel = true;
    }

    public String getCpf() {
        return cpf;
    }

    @Override
    public String toString() {
        return "Vaga{" + "cpf='" + cpf + '\'' + ", disponivel=" + disponivel + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vaga vaga = (Vaga) o;
        return Objects.equals(cpf, vaga.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }
}