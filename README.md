# 📱 HS iPhones API - ERP & PDV Especializado

Bem-vindo ao repositório da **HS iPhones API**.\
Este projeto consiste em um backend robusto, projetado para gerenciar de
forma completa a operação de uma loja especializada em smartphones, com
foco no ecossistema Apple.

A API cobre desde o processo de venda direta (PDV) até fluxos complexos
como BuyBack (retoma), recondicionamento interno e Assistência Técnica.

------------------------------------------------------------------------

## 🎯 Objetivo do Sistema

Diferente de ERPs genéricos de mercado, esta API foi modelada com base
nas regras de negócio reais de uma loja especializada em celulares.

Principais diferenciais:

-   **Rastreamento de IMEI:**\
    Cada aparelho é tratado como uma unidade única, garantindo controle
    individualizado de estoque.

-   **BuyBack & Refurbishment:**\
    Controle completo de aparelhos que entram como forma de pagamento,
    passam por reparos internos (com agregação de custo real de peças e
    serviços terceirizados) e retornam para venda.

-   **Assistência Técnica (externa):**\
    Gestão de Ordens de Serviço (O.S.), incluindo diagnóstico,
    aprovação, consumo automático de peças e cálculo de mão de obra.

-   **Frente de Caixa (PDV):**\
    Orçamentos, vendas diretas, split de pagamentos, controle de status
    e geração de termos e comprovantes.

------------------------------------------------------------------------

## 🛠️ Tecnologias Utilizadas

-   **Java & Spring Boot** (Spring Web, Data JPA, Validation)
-   **PostgreSQL** (Banco de dados relacional)
-   **Flyway** (Versionamento e migração de banco de dados)
-   **Spring Cloud OpenFeign** (Integração com APIs externas)
-   **Swagger / OpenAPI** (Documentação interativa da API)
-   **Docker & Docker Compose** (Infraestrutura de desenvolvimento)

------------------------------------------------------------------------

## 🚀 Como Executar o Projeto Localmente

### 📋 Pré-requisitos

-   Java JDK (versão definida no `pom.xml`)
-   Maven (ou utilize o Maven Wrapper `./mvnw`)
-   Docker e Docker Compose

------------------------------------------------------------------------

### 1️⃣ Clone o Repositório

``` bash
git clone https://github.com/harlanpierre/hs-iphones-api
cd hs-iphones-api
```

------------------------------------------------------------------------

### 2️⃣ Suba o Banco de Dados (PostgreSQL)

O projeto já possui um arquivo `docker-compose.yml` configurado.

Execute na raiz do projeto:

``` bash
docker-compose up -d
```

Isso iniciará o PostgreSQL na porta `5432`.\
As credenciais padrão estão configuradas no `application.properties`.

------------------------------------------------------------------------

### 3️⃣ Execute as Migrations

As migrations são executadas automaticamente pelo Flyway ao iniciar a
aplicação.\
Não é necessário rodar scripts SQL manualmente.

------------------------------------------------------------------------

### 4️⃣ Inicie a Aplicação

Utilize o Maven Wrapper:

``` bash
./mvnw spring-boot:run
```

------------------------------------------------------------------------

### 5️⃣ Acesse a Documentação (Swagger)

Com a aplicação em execução, acesse:

👉 http://localhost:8080/swagger-ui.html

Lá você poderá visualizar todos os endpoints, DTOs e testar a API
interativamente.

------------------------------------------------------------------------

## 🏗️ Estrutura de Módulos

### 🔹 /clients e /suppliers

Gestão de clientes e fornecedores com validações estritas (CPF/CNPJ,
regras de integridade e consistência).

### 🔹 /cep

Consulta inteligente com estratégia de fallback entre ViaCEP, BrasilAPI
e OpenCEP.

### 🔹 /products

Inventário inteligente com: - Gerador dinâmico de SKUs - Controle
individual por IMEI - Histórico de status - Gestão de disponibilidade

### 🔹 /repairs/internal

Controle de recondicionamento de aparelhos da própria loja (BuyBack),
incluindo: - Registro de peças utilizadas - Cálculo de custo agregado -
Controle de status (em reparo, disponível, consumido, etc.)

### 🔹 /services/os

Gestão de Ordens de Serviço para aparelhos de clientes externos: -
Diagnóstico - Orçamento - Aprovação - Consumo automático de peças -
Finalização e entrega

### 🔹 /sales

Motor de vendas (PDV) com: - Máquina de estados para orçamentos - Split
de pagamentos - Controle financeiro - Emissão de recibos e termos em
HTML

------------------------------------------------------------------------

## 📌 Arquitetura e Boas Práticas

O projeto segue boas práticas de mercado:

-   Separação em camadas (Controller → Service → Repository)
-   DTOs para isolamento da camada de transporte
-   Tratamento centralizado de exceções
-   Logs estruturados
-   Controle transacional
-   Versionamento de banco com Flyway

------------------------------------------------------------------------

## 📈 Visão Estratégica

A HS iPhones API não é apenas um backend de vendas, mas um ERP
especializado no segmento de smartphones.

Ela foi concebida para: - Garantir rastreabilidade total dos aparelhos -
Controlar custo real de recondicionamento - Integrar vendas e serviços
no mesmo ecossistema - Escalar para múltiplas unidades no futuro

------------------------------------------------------------------------

Desenvolvido para suportar crescimento, organização e alta
rastreabilidade operacional.
