package PUCRS_Estacionamento.TF;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Cliente {
    protected String cpf;
    protected String nome;

    protected Map<String, Veiculo> veiculos;
    protected List<UsoDeVaga> historico;

    public Cliente(String cpf, String nome) {
        this.cpf = cpf;
        this.nome = nome;
        this.veiculos = new HashMap<>();
        this.historico = new ArrayList<>();
    }

    public void addVeiculo(Veiculo v) {

        if (veiculos.containsKey(v.getPlaca())) {
            throw new IllegalArgumentException("Carro com placa " + v.getPlaca() + " já cadastrado.");
        }

        if (!(this instanceof Tecnopuc) && veiculos.size() >= 2) {
            throw new RuntimeException("Limite de veículos atingido para este cliente.");
        }

        veiculos.put(v.getPlaca(), v);
    }

    public void removerVeiculo(String placa) {

        if (veiculos.containsKey(placa)) {
            veiculos.remove(placa);
        } else {
            System.out.println("Aviso: Veículo " + placa + " não encontrado para remoção.");
        }
    }

    public boolean possuiVeiculo(String placa) {

        return veiculos.containsKey(placa);
    }

    public void addHistorico(UsoDeVaga uso) {
        historico.add(uso);
    }

    public List<UsoDeVaga> getHistorico() {
        return historico;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public List<Veiculo> getVeiculos() {
        return new ArrayList<>(veiculos.values());
    }

    public double getTotalArrecadado() {
        double total = 0;
        for (UsoDeVaga uso : historico) {
            if (uso.isPago()) {
                total += uso.getValorPago();
            }
        }
        return total;
    }

    public double getTotalGerado(GerenciadorEstacionamento ger) {

        double total = historico.stream()
                .mapToDouble(UsoDeVaga::getValorPago)
                .sum();

        for (Veiculo v : veiculos.values()) {
            UsoDeVaga usoAtual = ger.getUsoAtual(v.getPlaca());

            if (usoAtual != null) {

                LocalDateTime agora = LocalDateTime.now();
                double valorSimulado = this.calcularValor(usoAtual, agora);
                total += valorSimulado;
            }
        }

        return total;
    }

    public double getTotalGerado() {
        return historico.stream()
                .mapToDouble(UsoDeVaga::getValorPago)
                .sum();
    }

    public abstract double calcularValor(UsoDeVaga uso, LocalDateTime saida);

    public abstract void processarSaida(UsoDeVaga uso);
}