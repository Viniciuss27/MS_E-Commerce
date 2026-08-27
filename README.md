# MS_E-Commerce

Sistema de e-commerce desenvolvido com arquitetura de **microsserviços**, utilizando Spring Boot e Spring Cloud. O projeto foi desenvolvido com foco em comunicação entre serviços, descoberta de serviços, configuração centralizada, autenticação e autorização com JWT, processamento assíncrono com Apache Kafka e execução containerizada com Docker.

## Arquitetura

O projeto é composto pelos seguintes serviços:

* **Eureka Server** — descoberta e registro dos microsserviços.
* **Config Server** — gerenciamento centralizado das configurações.
* **API Gateway** — ponto de entrada da aplicação e roteamento das requisições.
* **Auth** — autenticação e geração de tokens JWT.
* **Categoria** — gerenciamento de categorias.
* **Cliente** — gerenciamento de clientes.
* **Produto** — gerenciamento de produtos e integração com o fluxo de estoque.
* **Pedido** — gerenciamento de pedidos e atualização de seus status.
* **Notificação** — processamento de eventos Kafka e consulta das notificações relacionadas aos pedidos.

### Fluxo simplificado

```text
                    ┌─────────────────┐
                    │      Cliente    │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │   API Gateway   │
                    │     :8765       │
                    └────────┬────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
              ▼              ▼              ▼
           Auth          Categoria       Cliente
              │              │              │
              └──────────────┼──────────────┘
                             │
              ┌──────────────┴──────────────┐
              │                             │
              ▼                             ▼
           Produto                        Pedido
                                             │
                                             │ Eventos
                                             ▼
                                      ┌────────────┐
                                      │   Kafka    │
                                      └─────┬──────┘
                                            │
                                            ▼
                                      Notificação
```

O **Eureka Server** é utilizado para descoberta dos serviços, enquanto o **Config Server** centraliza as configurações. O Gateway utiliza o **Spring Cloud LoadBalancer** juntamente com Eureka para localizar os serviços registrados.

## Tecnologias

### Backend

* Java 21
* Spring Boot 3.4.5
* Spring Cloud 2024.0.1
* Spring Data JPA
* Spring Security
* OAuth2 Resource Server
* JWT
* Spring Cloud Gateway
* Spring Cloud Netflix Eureka
* Spring Cloud Config
* Spring Cloud LoadBalancer
* Spring Kafka
* MapStruct
* Lombok
* Springdoc OpenAPI / Swagger

### Banco de dados e infraestrutura

* PostgreSQL
* Flyway
* Apache Kafka
* Docker
* Docker Compose

### Testes

* JUnit
* Mockito
* Spring Boot Test
* Spring Kafka Test

## Segurança

A aplicação utiliza **JWT (JSON Web Token)** para autenticação.

O fluxo de autenticação é:

```text
Cliente
   │
   ▼
POST /auth/login
   │
   ▼
Auth
   │
   ▼
JWT
   │
   ▼
API Gateway
   │
   ▼
Microsserviço
```

As operações protegidas utilizam autorização baseada em roles, permitindo diferenciar permissões entre usuários.

## Comunicação assíncrona

O projeto utiliza **Apache Kafka** para comunicação baseada em eventos.

Eventos relacionados ao processamento dos pedidos são publicados em tópicos Kafka e consumidos pelos serviços interessados.

Um dos fluxos implementados é:

```text
Pedido
   │
   ▼
Evento Kafka
   │
   ▼
Processamento de estoque
   │
   ├──────────────► Estoque reservado
   │
   └──────────────► Estoque falhou
                         │
                         ▼
                    Notificação
```

O microsserviço **Notificação** possui consumidores Kafka para eventos de estoque e persiste as notificações no PostgreSQL.

## Configuração centralizada

As configurações dos microsserviços são disponibilizadas pelo **Config Server**, utilizando um repositório externo de configuração.

Cada serviço pode carregar suas configurações de acordo com o profile utilizado, como `prod`.

Exemplo:

```yaml
spring:
  config:
    import:
      - optional:configserver:${SPRING_CLOUD_CONFIG_URI}
```

## Service Discovery

Os microsserviços são registrados no **Eureka Server**.

Exemplo:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://eureka:8761/eureka/
```

O Gateway utiliza os nomes registrados no Eureka para encaminhar as requisições:

```yaml
uri: lb://pedido
```

Dessa forma, o Gateway não precisa conhecer diretamente o endereço ou a porta dos microsserviços.

## API Gateway

O Gateway centraliza o acesso aos microsserviços.

Algumas rotas disponíveis:

| Serviço      | Rota               |
| ------------ | ------------------ |
| Auth         | `/auth/**`         |
| Categorias   | `/categorias/**`   |
| Clientes     | `/clientes/**`     |
| Pedidos      | `/pedidos/**`      |
| Produtos     | `/produtos/**`     |
| Notificações | `/notificacoes/**` |

Exemplo:

```text
GET http://localhost:8765/pedidos
```

O Gateway utiliza o LoadBalancer para localizar o serviço `pedido` através do Eureka.

## Banco de dados

Cada microsserviço possui sua própria configuração de banco PostgreSQL, seguindo a separação de responsabilidades característica de uma arquitetura de microsserviços.

As alterações de estrutura do banco são gerenciadas utilizando **Flyway**.

```text
Categoria  ──► PostgreSQL
Cliente    ──► PostgreSQL
Produto    ──► PostgreSQL
Pedido     ──► PostgreSQL
Notificação ─► PostgreSQL
```

## Docker

Os serviços são executados utilizando **Docker Compose**, permitindo subir a infraestrutura e os microsserviços de forma integrada.

Exemplo:

```bash
docker compose up -d --build
```

Para visualizar os containers:

```bash
docker compose ps
```

Para acompanhar os logs:

```bash
docker compose logs -f
```

Exemplo para acompanhar o serviço de notificações:

```bash
docker compose logs -f notificacao
```

## Endpoints principais

### Auth

```text
POST /auth/register
POST /auth/login
```

### Categorias

```text
GET    /categorias
POST   /categorias
PUT    /categorias/{id}
DELETE /categorias/{id}
```

### Clientes

```text
GET    /clientes
POST   /clientes
PUT    /clientes/{id}
DELETE /clientes/{id}
```

### Produtos

```text
GET    /produtos
POST   /produtos
PUT    /produtos/{id}
DELETE /produtos/{id}
```

### Pedidos

```text
GET  /pedidos
POST /pedidos
```

O serviço de pedidos também possui operações relacionadas à atualização de status.

### Notificações

```text
GET /notificacoes/pedido/{pedidoId}
```

Exemplo:

```text
GET /notificacoes/pedido/1
```

## Documentação da API

A aplicação utiliza **Springdoc OpenAPI** para documentação dos endpoints.

Com o Gateway em execução:

```text
http://localhost:8765/swagger-ui.html
```

## Estrutura geral

```text
MS_E-Commerce/
│
├── Auth/
├── Categoria/
├── Cliente/
├── Config_Server/
├── Eureka/
├── Gateway/
├── Notificacao/
├── Pedido/
├── Produto/
│
├── docker-compose.yml
└── .env
```

## Objetivos do projeto

O projeto foi desenvolvido para aplicar, na prática, conceitos de desenvolvimento de sistemas distribuídos e arquitetura de microsserviços, incluindo:

* Separação de responsabilidades entre serviços
* Service Discovery
* Configuração centralizada
* API Gateway
* Load Balancing
* Autenticação e autorização
* Comunicação síncrona entre serviços
* Comunicação assíncrona com Kafka
* Processamento baseado em eventos
* Persistência independente por serviço
* Migração de banco de dados
* Containerização
* Tratamento de exceções
* DTOs e mapeamento de objetos
* Testes automatizados

## Status

**Projeto concluído.**

O ambiente foi validado com os serviços executando em Docker Compose, incluindo comunicação através do API Gateway, descoberta via Eureka, autenticação JWT, processamento de eventos com Kafka e integração do serviço de Notificação com os eventos de estoque.

## Autor

**Vinicius De Jesus**

Desenvolvido como projeto prático para aprofundamento em **Java, Spring Boot, Spring Cloud e arquitetura de microsserviços**.
