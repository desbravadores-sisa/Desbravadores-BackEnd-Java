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

## 📌 Endpoints

### Autenticação e Usuários

| Método | Rota | Permissão | Descrição |
| --- | --- | --- | --- |
| POST | `/usuarios/cadastro` | Sem permissão | Cadastra um usuário |
| POST | `/usuarios/login` | Sem permissão | Autentica o usuário e cria o cookie `authToken` |
| POST | `/usuarios/logoff` | Usuário autenticado | Encerra a sessão removendo o cookie `authToken` |
| GET | `/usuarios/painel-diretoria` | Diretor | Valida acesso ao painel exclusivo da diretoria |

### Unidades

| Método | Rota | Permissão | Descrição |
| --- | --- | --- | --- |
| GET | `/unidades/diretor` | Diretor | Lista as unidades do clube do diretor autenticado |
| POST | `/unidades` | Diretor | Cadastra uma unidade no clube do diretor autenticado |
| PUT | `/unidades` | Diretor | Atualiza uma unidade |
| DELETE | `/unidades/{idUnidade}` | Diretor | Exclui uma unidade |
| GET | `/unidades/conselheiro` | Conselheiro | Busca a unidade vinculada ao conselheiro autenticado |

### Tarefas

| Método | Rota | Permissão | Descrição |
| --- | --- | --- | --- |
| GET | `/tarefas` | Sem permissão | Lista todas as tarefas |
| GET | `/tarefas/{id}` | Sem permissão | Busca uma tarefa específica |
| GET | `/tarefas/kanban` | Sem permissão | Lista as tarefas agrupadas por status do Kanban |
| POST | `/tarefas` | Diretor | Cria uma tarefa e o vínculo inicial com a unidade |
| PUT | `/tarefas/{id}` | Diretor | Atualiza os dados da tarefa |
| DELETE | `/tarefas/{id}` | Diretor | Exclui a tarefa e seu vínculo com a unidade |
| PATCH | `/tarefas/{id}/status` | Conselheiro | Atualiza o status da tarefa no Kanban |

### Tarefas Unidades

| Método | Rota | Permissão | Descrição |
| --- | --- | --- | --- |
| GET | `/tarefas-unidades/{idTarefa}` | Sem permissão | Visualiza o status da tarefa na unidade |
| PUT | `/tarefas-unidades/{idTarefa}/status` | Conselheiro | Move o status da tarefa no Kanban |

O vínculo `tarefas-unidades` é criado junto com `POST /tarefas` e removido junto com `DELETE /tarefas/{id}`.

### Evidências

| Método | Rota | Permissão | Descrição |
| --- | --- | --- | --- |
| POST | `/evidencias` | Conselheiro | Anexa uma evidência a uma tarefa da unidade do conselheiro |
| GET | `/evidencias` | Diretor | Lista as evidências do clube do diretor autenticado |
| GET | `/evidencias/unidade` | Conselheiro | Lista as evidências da unidade do conselheiro autenticado |
| PUT | `/evidencias/{id}` | Conselheiro | Edita uma evidência da unidade do conselheiro |
| DELETE | `/evidencias/{id}` | Conselheiro | Deleta uma evidência da unidade do conselheiro quando a tarefa não está concluída |

O `DELETE /evidencias/{id}` é bloqueado quando a tarefa vinculada à evidência está com status `Concluído`.

### Documentação e Ferramentas

| Método | Rota | Permissão | Descrição |
| --- | --- | --- | --- |
| GET | `/swagger-ui.html` | Sem permissão | Redireciona para a interface do Swagger |
| GET | `/swagger-ui/**` | Sem permissão | Interface visual da documentação OpenAPI |
| GET | `/v3/api-docs/**` | Sem permissão | Especificação OpenAPI gerada pela aplicação |
| GET | `/h2-console/**` | Sem permissão | Console web do H2 quando habilitado no ambiente |
