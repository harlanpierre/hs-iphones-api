# 📱 HS iPhones API - ERP & PDV Especializado

Bem-vindo ao repositório da **HS iPhones API**. Este projeto é um backend robusto construído para gerenciar toda a operação de uma loja especializada em smartphones (com foco no ecossistema Apple), cobrindo desde a venda direta até processos complexos como BuyBack (retoma) e Assistência Técnica.

## 🎯 O que o sistema resolve?
Diferente de ERPs genéricos de mercado, esta API foi modelada com as regras de negócio reais de uma loja de celulares:
* **Rastreamento de IMEI:** Cada aparelho é tratado de forma única.
* **BuyBack & Refurbishment:** Controle de aparelhos que entram como forma de pagamento, recebem reparos internos (agregando custo real de peças e terceirização ao aparelho) e voltam para a vitrine.
* **Assistência Técnica Externa:** Gestão de Ordens de Serviço (O.S.) com dedução automática de peças do estoque e cálculo de mão de obra.
* **Frente de Caixa (PDV):** Orçamentos, vendas diretas, split de pagamentos e geração de Termos de Garantia.

## 🛠️ Tecnologias Utilizadas
* **Java & Spring Boot** (Web, Data JPA, Validation)
* **PostgreSQL** (Banco de dados relacional)
* **Flyway** (Versionamento e migração de banco de dados)
* **Spring Cloud OpenFeign** (Integração com APIs externas)
* **Swagger / OpenAPI** (Documentação interativa)
* **Docker & Docker Compose** (Infraestrutura de desenvolvimento)

## 🚀 Como executar o projeto localmente

### Pré-requisitos
* Java JDK instalado (versão configurada no `pom.xml`).
* Maven (ou use o Wrapper `./mvnw` incluso no projeto).
* Docker e Docker Compose instalados.

### Passo a Passo

1. **Clone o repositório:**
   ```bash
   git clone [https://github.com/harlanpierre/hs-iphones-api](https://github.com/harlanpierre/hs-iphones-api)
   cd hs-iphones-api