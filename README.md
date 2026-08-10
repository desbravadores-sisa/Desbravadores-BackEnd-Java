# Desbravadores BackEnd Java

Repositorio do backend Java/Spring Boot do sistema Desbravadores.

Na branch `integrar-api-tasks`, a aplicacao principal `APIDesbravadores`
tambem recebeu os endpoints de tarefas que antes estavam em uma API separada.
A partir desta reorganizacao, a API principal para execucao e evolucao e apenas
a `APIDesbravadores`.

## Estrutura

```text
APIDesbravadores/   API principal do sistema
README.md           Documentacao do repositorio
```

## Responsabilidade deste repositorio

- API REST do sistema.
- Autenticacao e autorizacao com Spring Security/JWT.
- Cadastro, login e logoff de usuarios.
- Gestao de unidades.
- Gestao de tarefas e quadro Kanban.
- Testes unitarios/de controller com JUnit e MockMvc.

Os scripts e a modelagem do banco **nao ficam neste repositorio**. Eles devem
ser mantidos no repositorio `Desbravadores-Banco-De-Dados`.

## Tecnologias

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Security
- Spring Data JPA
- JWT
- Maven
- JUnit
- MockMvc
- MySQL via variaveis de ambiente

## Configuracao

A API principal le as configuracoes sensiveis por variaveis de ambiente em
`APIDesbravadores/src/main/resources/application.properties`:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
api.security.token.secret=${JWT_SECRET}
```

Exemplo local:

```powershell
cd APIDesbravadores
Copy-Item .env.example .env
```

Depois ajuste os valores no `.env` de acordo com seu MySQL local. O arquivo
`.env` nao deve ser versionado.

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/desbravadores"
$env:DB_USER="jpauser"
$env:DB_PASSWORD="senha-segura123"
$env:JWT_SECRET="uma-chave-local-para-desenvolvimento"
```

Nao versionar `.env`, senhas, dumps ou scripts de banco neste repositorio.

## Como executar

```bash
cd APIDesbravadores
./mvnw.cmd spring-boot:run
```

URLs comuns:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Endpoints

### Usuarios

```http
POST /usuarios/cadastro
POST /usuarios/login
POST /usuarios/logoff
GET  /usuarios/painel-diretoria
```

### Unidades

```http
GET    /unidades/diretor
POST   /unidades
PUT    /unidades
DELETE /unidades/{idUnidade}
GET    /unidades/conselheiro
```

### Tarefas

```http
POST   /tarefas
GET    /tarefas
GET    /tarefas/{id}
PUT    /tarefas/{id}
DELETE /tarefas/{id}
PATCH  /tarefas/{id}/status
GET    /tarefas/kanban
```

Total atual: 16 endpoints.

## Testes

Os testes dos endpoints ficam em:

```text
APIDesbravadores/src/test/java/school/sptech/APIDesbravadores/controller/
```

Para executar:

```bash
cd APIDesbravadores
./mvnw.cmd test
```

A suite cobre os controllers de usuarios, unidades e tarefas usando JUnit,
MockMvc e services mockados, sem depender de banco real.

## Observacoes

- A pasta `target/` e ignorada pelo Git.
- Arquivos de banco devem ficar apenas em `Desbravadores-Banco-De-Dados`.
- Para alterar estrutura de tabelas, atualizar primeiro o repositorio de banco
  e depois alinhar as entidades JPA neste backend.
