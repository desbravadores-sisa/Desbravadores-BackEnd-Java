# 🚀 API Desbravadores & Tasks

Sistema backend desenvolvido com **Java + Spring Boot**, responsável pelo gerenciamento de usuários, autenticação e atividades do clube de Desbravadores.

O projeto foi estruturado com foco em **boas práticas de desenvolvimento**, organização em camadas e escalabilidade.

---

## 🧱 Arquitetura

A aplicação segue o padrão de arquitetura em camadas (**Layered Architecture**), promovendo separação de responsabilidades e facilidade de manutenção.

### 📁 Estrutura de Pastas

#### 🔐 config
Configurações da aplicação:
- Segurança (Spring Security)
- Autenticação (JWT, filtros)
- Configurações globais

#### 🌐 controller
Camada de entrada (API REST):
- Recebe requisições HTTP
- Retorna respostas ao cliente

#### 🧠 service
Regras de negócio:
- Processamento de dados
- Validações e lógica da aplicação

#### 🗄️ repository
Acesso ao banco de dados:
- Interfaces JPA
- Operações CRUD

#### 📦 domain
Entidades do sistema:
- Representação das tabelas do banco

#### 🔄 dto
Objetos de transferência de dados:
- Entrada e saída da API
- Evita expor entidades diretamente

#### 🔁 mapper
Conversão entre objetos:
- Entity ⇄ DTO

#### ⚠️ exception
Tratamento de erros:
- Exceções personalizadas
- Respostas padronizadas

---

## ⚙️ Tecnologias Utilizadas

- Java 17+
- Spring Boot
- Spring Security
- Spring Data JPA
- Maven
- Banco de Dados Relacional (MySQL/PostgreSQL)

---

## ▶️ Como Executar

### Pré-requisitos
- Java instalado
- Maven instalado
- Banco de dados configurado

### Passos

```bash
# Clone o repositório
git clone <URL_DO_REPOSITORIO>

# Entre na pasta do projeto
cd nome-do-projeto
