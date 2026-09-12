<div align="center">

# Sistema de Gerenciamento de Estacionamento — PUCRS

### Projeto Final da disciplina de Programação Orientada a Objetos (POO)

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Vaadin](https://img.shields.io/badge/Vaadin_24-00B4F0?style=for-the-badge&logo=vaadin&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

</div>

---

## Sobre o projeto

Sistema de gerenciamento de estacionamento desenvolvido como projeto final da disciplina de **Programação Orientada a Objetos (PUCRS)**. A aplicação controla o cadastro de clientes e veículos, o uso de vagas e a tarifação diferenciada conforme o tipo de cliente — aplicando na prática conceitos de herança, polimorfismo, encapsulamento e o padrão de projeto Singleton.

O backend é implementado em **Java puro**, com toda a lógica de negócio (clientes, vagas, tarifação, histórico de uso) modelada via orientação a objetos, sem depender de um framework para as regras do domínio. A interface web é construída com **Vaadin**, que utiliza o Spring Boot internamente apenas como mecanismo de inicialização do servidor — não como camada de negócio.

## Funcionalidades

- Cadastro de **clientes**, com regras de tarifação diferentes para cada perfil:
  - `Estudante` — desconto para estudantes da universidade
  - `Pucrs` — tarifa para vínculo direto com a PUCRS
  - `Tecnopuc` — tarifa para empresas/colaboradores do Tecnopuc
- Cadastro e controle de **veículos** por cliente
- Controle de **vagas** disponíveis e ocupadas
- Registro de **uso de vaga** (entrada e saída), com cálculo de tempo e valor a pagar
- Gerenciamento centralizado via `GerenciadorEstacionamento`, implementado como **Singleton**

## Arquitetura

```
Estacionamento_PUCRS/
├── frontend/                          # Views Vaadin (interface web)
├── src/main/java/PUCRS_Estacionamento/TF/
│   ├── App.java                       # Ponto de entrada da aplicação
│   ├── Cliente.java                   # Classe base dos tipos de cliente
│   ├── Estudante.java                 # Cliente com tarifa de estudante
│   ├── Pucrs.java                     # Cliente com tarifa PUCRS
│   ├── Tecnopuc.java                  # Cliente com tarifa Tecnopuc
│   ├── Veiculo.java                   # Dados do veículo
│   ├── Vaga.java                      # Vagas do estacionamento
│   ├── UsoDeVaga.java                 # Registro de entrada/saída e cobrança
│   └── GerenciadorEstacionamento.java # Regras de negócio e orquestração
└── pom.xml
```

## Como rodar

```bash
cd Estacionamento_PUCRS
mvn clean install
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

## Equipe

- Marco Antônio De Carli Rodegheri
- Luiz Henrique Saggin Confortin
- Roger Rozales Ehlert
