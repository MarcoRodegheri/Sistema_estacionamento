<div align="center">

# Sistema de Estacionamento da PUCRS

### Trabalho Final da disciplina de Programação Orientada a Objetos (POO)

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Vaadin](https://img.shields.io/badge/Vaadin_24-00B4F0?style=for-the-badge&logo=vaadin&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

</div>

---

## Sobre o projeto

Sistema de controle dos estacionamentos da PUCRS, desenvolvido em **Java puro** como trabalho final da disciplina de **Programação Orientada a Objetos**. A aplicação gerencia o acesso e a cobrança de três tipos de usuário — funcionários PUCRS, estudantes e profissionais de empresas do Tecnopuc — cada um com uma regra de tarifação diferente, controlando entrada, saída, créditos e relatórios de uso do estacionamento.

O projeto consolida os principais conceitos de orientação a objetos vistos na disciplina — **herança**, **polimorfismo**, **coleções**, **expressões lambda** e **streams** — aplicados a um domínio de negócio real, com uma interface web construída em **Vaadin**.

## Como funciona

O sistema é organizado em três frentes, conforme o enunciado do trabalho:

1. **Controle de acesso (cancelas)**
   - **Entrada** — registra a placa, data e hora de entrada de um veículo, validando as regras do tipo de usuário (limite de veículos, saldo devedor, lotação do estacionamento). Se o estacionamento estiver cheio (capacidade de 500 vagas) ou a validação falhar, a entrada é recusada.
   - **Saída** — registra a saída pela placa do veículo e calcula a cobrança de acordo com o tipo de usuário:
     - **Funcionários PUCRS**: até 2 veículos cadastrados (um em uso por vez), sem cobrança de estacionamento.
     - **Estudantes**: até 2 veículos cadastrados (um em uso por vez), cobrança fixa de R$ 15,00 por entrada, debitada do cartão do estudante. Estadias de menos de 15 minutos não são cobradas, e o aluno é notificado quando o saldo devedor atinge R$ 15,00.
     - **Tecnopuc**: sem limite de veículos por empresa, cobrança mensal de R$ 1,50 por hora de uso.

2. **Módulo financeiro**
   - Carregamento de créditos para estudantes, nos valores válidos de R$ 15, 50, 100 e 150.
   - Geração de boletos mensais para as empresas do Tecnopuc, com registro de pagamento.

3. **Módulo gerencial**
   - Cadastro dos diferentes tipos de usuário e de seus veículos.
   - Relatórios de entradas por tipo de usuário e por intervalo de datas.
   - Cálculo da receita total gerada em um determinado mês/ano.
   - Relatório de uso do estacionamento filtrado por usuário e período, utilizando **streams** e operações de agregação sobre as coleções de registros.

O sistema carrega um conjunto inicial de dados a partir de arquivos texto, permitindo testar todas as funcionalidades e relatórios sem a necessidade de cadastro manual prévio.

## Estrutura do projeto

```
Sistema_estacionamento/
├── Estacionamento_PUCRS/
│   ├── frontend/                          # Views Vaadin (interface web)
│   ├── src/main/java/PUCRS_Estacionamento/TF/
│   │   ├── App.java                       # Ponto de entrada da aplicação
│   │   ├── AppShell.java                  # Configuração do shell/tema da aplicação Vaadin
│   │   ├── Cliente.java                   # Classe base dos tipos de cliente
│   │   ├── Estudante.java                 # Cliente com tarifa e regras de estudante
│   │   ├── Pucrs.java                     # Cliente com tarifa de funcionário PUCRS
│   │   ├── Tecnopuc.java                  # Cliente com tarifa de empresa Tecnopuc
│   │   ├── Veiculo.java                   # Dados do veículo
│   │   ├── Vaga.java                      # Vagas do estacionamento
│   │   ├── UsoDeVaga.java                 # Registro de entrada/saída e cobrança
│   │   └── GerenciadorEstacionamento.java # Regras de negócio e orquestração (Singleton)
│   └── pom.xml
└── README.md
```

## Como rodar

```bash
cd Estacionamento_PUCRS
mvn clean install
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

## Autor

- Marco Antônio De Carli Rodegheri
- Luiz Henrique Saggin Confortin
- Roger Rozales Ehlert
