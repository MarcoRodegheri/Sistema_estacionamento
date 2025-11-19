package PUCRS_Estacionamento.TF;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class GerenciadorEstacionamento {

    private static GerenciadorEstacionamento instance;
    private Map<String, Cliente> clientes;
    private Map<String, UsoDeVaga> veiculosEstacionados;
    private List<Vaga> vagas;

    private GerenciadorEstacionamento() {
        clientes = new HashMap<>();
        veiculosEstacionados = new HashMap<>();
        vagas = new ArrayList<>();

        for (int i = 1; i <= 500; i++) {
            vagas.add(new Vaga("V" + i));
        }

        carregarClientes();
        carregarEntradasIniciais();
    }

    public static GerenciadorEstacionamento getInstance() {
        if (instance == null) {
            instance = new GerenciadorEstacionamento();
        }
        return instance;
    }

    private void carregarClientes() {
        try {
            if (!Files.exists(Paths.get("clientes.txt")))
                return;

            List<String> linhas = Files.readAllLines(Paths.get("clientes.txt"));
            for (String linha : linhas) {
                if (linha.trim().isEmpty())
                    continue;

                String[] dados = linha.split(",");
                String cpf = dados[0];
                String nome = dados[1];
                String tipo = dados[4];

                Cliente novoCliente = null;

                if (tipo.equalsIgnoreCase("Estudante")) {
                    novoCliente = new Estudante(cpf, nome);
                    try {
                        ((Estudante) novoCliente).recarregar(Double.parseDouble(dados[3]));
                    } catch (Exception e) {
                    }
                } else if (tipo.equalsIgnoreCase("Tecnopuc")) {
                    novoCliente = new Tecnopuc(cpf, nome);
                } else {
                    novoCliente = new Pucrs(cpf, nome);
                }

                if (novoCliente != null) {
                    for (int i = 5; i < dados.length; i++) {
                        if (!dados[i].isEmpty()) {
                            novoCliente.addVeiculo(new Veiculo(dados[i]));
                        }
                    }
                    clientes.put(cpf, novoCliente);
                }
            }
        } catch (IOException e) {
            System.out.println("Aviso: Não foi possível ler clientes.txt. Iniciando vazio.");
        }
    }

    private void carregarEntradasIniciais() {
        try {
            if (!Files.exists(Paths.get("entradas.txt")))
                return;

            List<String> linhas = Files.readAllLines(Paths.get("entradas.txt"));
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (String linha : linhas) {
                String[] partes = linha.split(",");
                String placa = partes[0];
                LocalDateTime dataHora = LocalDateTime.parse(partes[1] + " " + partes[2], dtf);

                Cliente dono = null;
                for (Cliente c : clientes.values()) {
                    if (c.possuiVeiculo(placa)) {
                        dono = c;
                        break;
                    }
                }

                if (dono != null) {
                    try {
                        registrarEntrada(placa, dataHora);
                    } catch (Exception e) {

                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar entradas: " + e.getMessage());
        }
    }

    public void cadastrarCliente(Cliente novoCliente) {
        if (clientes.containsKey(novoCliente.getCpf())) {
            clientes.put(novoCliente.getCpf(), novoCliente);
            return;
        }
        clientes.put(novoCliente.getCpf(), novoCliente);
    }

    public String registrarEntrada(String placa, LocalDateTime dataHora) {
        if (veiculosEstacionados.containsKey(placa)) {
            throw new IllegalStateException("Este veículo já está estacionado.");
        }

        Cliente cliente = clientes.values().stream()
                .filter(c -> c.possuiVeiculo(placa))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Placa não cadastrada!"));

        for (Veiculo v : cliente.getVeiculos()) {
            if (veiculosEstacionados.containsKey(v.getPlaca())) {
                throw new IllegalStateException("Entrada Bloqueada: O cliente já possui outro veículo no pátio.");
            }
        }

        String aviso = null;

        if (cliente instanceof Estudante) {
            Estudante est = (Estudante) cliente;

            if (est.getSaldo() < -15.0) {
                throw new IllegalStateException("Entrada bloqueada: Dívida de R$ "
                        + String.format("%.2f", est.getSaldo()) + " excede o limite.");
            }

            if (est.getSaldo() < 0) {
                aviso = "ATENÇÃO: Saldo devedor de R$ " + String.format("%.2f", est.getSaldo())
                        + ". Regularize sua situação.";
            }
        }

        Vaga vagaLivre = null;
        for (Vaga v : vagas) {
            if (v.isDisponivel()) {
                vagaLivre = v;
                break;
            }
        }

        if (vagaLivre == null) {
            throw new IllegalStateException("Estacionamento lotado!");
        }

        vagaLivre.ocupar();
        UsoDeVaga uso = new UsoDeVaga(placa, vagaLivre, dataHora);
        veiculosEstacionados.put(placa, uso);

        return aviso;
    }

    public void registrarSaida(String placa, LocalDateTime saida) {
        UsoDeVaga uso = veiculosEstacionados.get(placa);
        if (uso == null)
            throw new IllegalArgumentException("Veículo não encontrado no pátio.");

        Cliente cliente = clientes.values().stream().filter(c -> c.possuiVeiculo(placa)).findFirst().orElse(null);

        if (cliente == null) {
            uso.registrarSaida(saida, 0.0);
            uso.getVaga().liberar();
            veiculosEstacionados.remove(placa);
            return;
        }

        double valor = 0.0;
        long minutos = ChronoUnit.MINUTES.between(uso.getEntrada(), saida);

        if (cliente instanceof Estudante || minutos >= 15) {
            valor = cliente.calcularValor(uso, saida);
        }

        uso.registrarSaida(saida, valor);
        cliente.processarSaida(uso);
        cliente.addHistorico(uso);

        uso.getVaga().liberar();
        veiculosEstacionados.remove(placa);
    }

    public Cliente getCliente(String cpf) {
        return clientes.get(cpf);
    }

    public int getTotalVeiculosNoEstacionamento() {
        return veiculosEstacionados.size();
}

    public Map<String, Integer> relatorioEntradasPorTipo() {
        return relatorioEntradasPorTipo(LocalDateTime.MIN, LocalDateTime.MAX);
    }

    public Map<String, Integer> relatorioEntradasPorTipo(LocalDateTime inicio, LocalDateTime fim) {
        Map<String, Integer> contador = new HashMap<>();
        contador.put("Estudante", 0);
        contador.put("Tecnopuc", 0);
        contador.put("Pucrs", 0);

        for (Cliente c : clientes.values()) {
            for (UsoDeVaga uso : c.getHistorico()) {
                if (uso.getEntrada().isAfter(inicio) && uso.getEntrada().isBefore(fim)) {
                    if (c instanceof Estudante)
                        contador.put("Estudante", contador.get("Estudante") + 1);
                    else if (c instanceof Tecnopuc)
                        contador.put("Tecnopuc", contador.get("Tecnopuc") + 1);
                    else
                        contador.put("Pucrs", contador.get("Pucrs") + 1);
                }
            }
        }
        return contador;
    }

    public double relatorioReceitaMensal(int mes, int ano) {
        double total = 0;
        for (Cliente c : clientes.values()) {
            for (UsoDeVaga uso : c.getHistorico()) {
                if (uso.isPago() && uso.getSaida() != null) {
                    if (uso.getSaida().getMonthValue() == mes && uso.getSaida().getYear() == ano) {
                        total += uso.getValorPago();
                    }
                }
            }
        }
        return total;
    }

    public String relatorioUsoCliente(String cpf, LocalDateTime inicio, LocalDateTime fim) {
        Cliente c = clientes.get(cpf);
        if (c == null)
            return "Cliente não encontrado.";

        StringBuilder sb = new StringBuilder();
        sb.append("Histórico de ").append(c.getNome()).append(":\n");

        boolean achou = false;
        for (UsoDeVaga uso : c.getHistorico()) {
            if (uso.getEntrada().isAfter(inicio) && uso.getEntrada().isBefore(fim)) {
                sb.append(uso.toString()).append("\n");
                achou = true;
            }
        }

        if (!achou)
            sb.append("Nenhum uso neste período.");
        return sb.toString();
    }

    public double consultarValorMedioPorUso() {
        return clientes.values().stream()
                .flatMap(c -> c.getHistorico().stream())
                .filter(uso -> uso.getValorPago() > 0.0)
                .mapToDouble(UsoDeVaga::getValorPago)
                .average()
                .orElse(0.0);
    }

    public List<Cliente> getTodosClientes() {
        return new ArrayList<>(clientes.values());
    }

    public List<UsoDeVaga> getVeiculosNoPatio() {
        return new ArrayList<>(veiculosEstacionados.values());
    }

    public void recarregarCredito(String cpf, double valor) {
        Cliente c = clientes.get(cpf);
        if (c instanceof Estudante)
            ((Estudante) c).recarregar(valor);
        else
            throw new IllegalArgumentException("Cliente não é estudante.");
    }

    public String gerarBoleto(String cpf, int mes, int ano) {
        Cliente c = clientes.get(cpf);
        if (c instanceof Tecnopuc)
            return ((Tecnopuc) c).gerarBoleto(mes, ano);
        return "Erro: Cliente não é Tecnopuc.";
    }

    public void pagarBoleto(String cpf, int mes, int ano) {
        Cliente c = clientes.get(cpf);
        if (c instanceof Tecnopuc)
            ((Tecnopuc) c).pagarBoleto(mes, ano);
    }

    public UsoDeVaga getUsoAtual(String placa) {
        return veiculosEstacionados.get(placa);
    }

    public List<String> relatorioTop5Clientes() {
        return clientes.values().stream()
                .sorted((c1, c2) -> Double.compare(c2.getTotalGerado(this), c1.getTotalGerado(this)))
                .limit(5)
                .map(c -> c.getNome() + " - R$ " + String.format("%.2f", c.getTotalGerado(this)))
                .collect(Collectors.toList());
    }

}