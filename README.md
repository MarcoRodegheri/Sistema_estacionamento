<div align="center">

# Sistema de Gerenciamento de Estacionamento — PUCRS

### Projeto Final da disciplina de Programação Orientada a Objetos (POO)

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Vaadin](https://img.shields.io/badge/Vaadin_24-00B4F0?style=for-the-badge&logo=vaadin&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

</div>

---

## Sobre o projeto

Sistema de gerenciamento de estacionamento desenvolvido como projeto final da disciplina de **Programação Orientada a Objetos (PUCRS)**. A aplicação controla o cadastro de clientes e veículos, o uso de vagas e a tarifação diferenciada conforme o tipo de cliente — aplicando na prática conceitos de herança, polimorfismo e encapsulamento.

O sistema possui backend em **Spring Boot** e uma interface web construída com **Vaadin**, permitindo interação direta com o gerenciador de estacionamento pelo navegador.

## Funcionalidades

- Cadastro de **clientes**, com regras de tarifação diferentes para cada perfil:
  - `Estudante` — desconto para estudantes da universidade
  - `Pucrs` — tarifa para vínculo direto com a PUCRS
  - `Tecnopuc` — tarifa para empresas/colaboradores do Tecnopuc
- Cadastro e controle de **veículos** por cliente
- Controle de **vagas** disponíveis e ocupadas
- Registro de **uso de vaga** (entrada e saída), com cálculo de tempo e valor a pagar
- Gerenciamento centralizado via `GerenciadorEstacionamento`

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

## 👥 Equipe — Grupo 14

- Marco Antônio De Carli Rodegheri
- Luiz Henrique Saggin Confortin
- Roger Rozales Ehlert

## 🔗 Contato

[LinkedIn](https://www.linkedin.com/in/marco-rodegheri/) · [marcoantoniorodegheri@gmail.com](mailto:marcoantoniorodegheri@gmail.com)
