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

```

---

## 📌 Endpoints de Tarefas

### Tarefas

| Método | Rota | Permissão | Descrição |
| --- | --- | --- | --- |
| GET | `/tarefas` | Sem permissão | Lista todas as tarefas |
| GET | `/tarefas/{id}` | Sem permissão | Busca uma tarefa específica |
| POST | `/tarefas` | Diretor | Cria uma tarefa e o vínculo inicial com a unidade |
| PUT | `/tarefas/{id}` | Diretor | Atualiza os dados da tarefa |
| DELETE | `/tarefas/{id}` | Diretor | Exclui a tarefa e seu vínculo com a unidade |

### Tarefas Unidades

| Método | Rota | Permissão | Descrição |
| --- | --- | --- | --- |
| GET | `/tarefas-unidades/{idTarefa}` | Sem permissão | Visualiza o status da tarefa na unidade |
| PUT | `/tarefas-unidades/{idTarefa}/status` | Conselheiro | Move o status da tarefa no Kanban |

O vínculo `tarefas-unidades` é criado junto com `POST /tarefas` e removido junto com `DELETE /tarefas/{id}`.
