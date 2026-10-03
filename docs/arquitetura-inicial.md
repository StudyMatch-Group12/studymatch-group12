# Arquitetura de Software Inicial

Este documento descreve a proposta de arquitetura inicial para a aplicação full-stack **StudyMatch**, detalhando a responsabilidade de cada camada e os mecanismos de comunicação.

## 1. Diagrama de Arquitetura em Camadas

A arquitetura do StudyMatch segue um modelo em camadas (*Layered Architecture*), promovendo a separação de responsabilidades e o desacoplamento entre componentes.

```mermaid
graph TD
    subgraph Frontend ["Camada de Apresentação (Frontend)"]
        UI["React SPA (Componentes / Vistas)"]
    end

    subgraph Backend ["Camada de Aplicação / Backend (Spring Boot)"]
        API["Camada REST API (Controllers)"]
        BL["Lógica de Negócio (Services & Domain Model)"]
        DAL["Camada de Persistência (Repositories - Spring Data JPA)"]
    end

    subgraph Database ["Camada de Dados"]
        DB[(Base de Dados MySQL)]
    end

    UI -- "Requisições HTTP/REST (JSON / CORS)" --> API
    API --> BL
    BL --> DAL
    DAL -- "Queries SQL / JDBC" --> DB
