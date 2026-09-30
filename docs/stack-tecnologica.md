# Stack Tecnológica

Este documento define e justifica as tecnologias selecionadas para o projeto StudyMatch.

## 1. Tecnologias Selecionadas
* **Frontend:** React
* **Backend:** Java com Spring Boot
* **Persistência:** MySQL
* **Build/Dependências:** Gradle

## 2. Justificação 

* **Testabilidade:** O ecossistema React facilita testes isolados aos componentes da interface, enquanto a injeção de dependências do Spring Boot (Backend) torna extremamente simples a criação de testes unitários e de integração mockados.
* **Manutenibilidade:** A utilização do Java oferece tipagem forte, o que reduz erros em tempo de execução. O Spring Boot impõe uma arquitetura organizada e em camadas, enquanto o React, ao utilizar componentes reutilizáveis, evita a duplicação de código.
* **Integração:** O Spring Boot cria APIs RESTful de forma nativa e eficiente, que comunicam de forma transparente com o Frontend em React através de JSON. O Gradle simplifica a orquestração do build de toda a aplicação Backend e da gestão de dependências.
* **Conhecimento da Equipa:** A escolha do Java baseia-se na experiência prévia da equipa em programação orientada a objetos (no IntelliJ IDEA). O MySQL foi selecionado pela forte familiaridade da equipa com bases de dados relacionais, linguagem SQL, diagramas Entidade-Relacionamento e *stored procedures*, o que garante uma transição rápida e estruturada na gestão do histórico académico dos estudantes.
