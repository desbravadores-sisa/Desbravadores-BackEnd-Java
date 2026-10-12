# Desbravadores BackEnd Java

API REST (Java 21 + Spring Boot 4) do sistema de gestão do Clube de Desbravadores **Tigre da Montanha** (Projeto Integrador SPTech).

| Repositório | Papel |
|---|---|
| [Desbravadores-Aplicacao-Web](https://github.com/desbravadores-sisa/Desbravadores-Aplicacao-Web) | Frontend React, que consome esta API |
| **Desbravadores-BackEnd-Java** (este) | API REST |
| [Desbravadores-Banco-De-Dados](https://github.com/desbravadores-sisa/Desbravadores-Banco-De-Dados) | Scripts MySQL. **Única fonte do schema** |

🎨 Protótipo de referência: [Figma Make — prototipo_versãofinal](https://www.figma.com/make/zdjDwsWCbkBmPXUXtR9QxT/prototipo_vers%C3%A3ofinal?t=SkffTCtZwgP5uuL2-1)

---

## Em que pé estamos (11/10/2026)

A branch **`consolidacao/sprint-final`** é a versão consolidada e integrada ao frontend. Ela foi criada a partir de `feature/endpoints-domains`; a `main` local antiga tinha um merge com 43 conflitos e não compilava. A `main` recebe essa versão via Pull Request.

O que foi entregue nesta etapa:

- **Autorização corrigida.** Os perfis no banco são `Diretoria` e `Conselheiro` (authority `ROLE_DIRETORIA` / `ROLE_CONSELHEIRO`). O uso de `hasRole('DIRETOR')` bloqueava com 403 todos os usuários reais.
- **Contratos alinhados ao front.** O clube vem sempre do usuário logado (o front não envia `idClube`) em tarefas, evidências, convites e no resumo de cadernos.
- **Cadastro por convite** (`token`), com os códigos 404, 410, 422 e 409; o convite passa a `ACEITO`.
- **Perfil e senha:** `GET/PUT /usuarios/perfil` e `PUT /usuarios/senha`.
- **Tarefas:** sem unidade informada, a tarefa é distribuída para todas as unidades do clube. As consultas funcionam com vários vínculos por tarefa.
- **Evidências:** a decisão da Diretoria (`APROVADA` / `RECONHECIDO` / `CORRECAO_SOLICITADA`) atualiza `Unidade_Tarefa.status_kanban`. É possível ajustar os pontos concedidos, que entram no ranking por `vw_resumo_unidades`.
- **Notificações:** `GET /notificacoes` e marcação de lidas; são geradas automaticamente quando uma evidência é decidida.
- **107 testes passando**: os testes de controller e mais 14 testes de serviço em `IntegracaoFrontServiceTest`.
- **Validação E2E:** MySQL 8.4 criado com os scripts do repo de banco, esta API e os payloads reais do front. Resultado: 41/41 verificações.

### Limitações conhecidas

| Tema | Situação |
|---|---|
| Conselheiro | ✅ Kanban da unidade (`GET /tarefas/kanban`) e mudança de status no vínculo da unidade do conselheiro. Faltam o check-in com transição automática e o upload de evidência |
| Check-in de caderno | Existe CRUD de `/execucoes-caderno` e `/checklists-caderno`, mas **não** há a transição automática Em andamento ↔ Em revisão prevista no protótipo |
| Evidência (envio) | `POST /evidencias` exige `urlS3`; não há upload nem armazenamento de arquivos |
| Dashboard da unidade | ✅ `GET /unidades/{id}/dashboard`. "Desconto" é calculado como pontos previstos não obtidos (prazo vencido ou nota abaixo da prevista); o time precisa confirmar essa definição |
| Logs | Há 10 `System.out.println` de depuração (`AutenticacaoProvider`, `AutenticacaoService`, `ConviteController`, `ConviteService`, `UnidadeController`); alguns imprimem entidades `Usuario` inteiras, **com o hash da senha**, e convites com o token |
| Segurança | `GET` em `/tarefas/**`, `/evidencias/**`, `/cadernos/**`, `/clubes/**` etc. é liberado sem login; sem sessão, os dados de todos os clubes ficam visíveis |
| Nomes de tabela | O schema usa `Usuario`, `Convite`…, enquanto o Hibernate consulta em minúsculas. Funciona apenas em MySQL que não diferencia maiúsculas (Windows ou `lower_case_table_names=1`) |

---

## Configuração

As variáveis ficam no arquivo `APIDesbravadores/.env` (carregado automaticamente) ou no ambiente:

```properties
DB_URL=jdbc:mysql://localhost:3306/clube_desbravadores
DB_USER=...
DB_PASSWORD=...
JWT_SECRET=...   # precisa ser Base64 (ex.: 64 bytes aleatórios); com caracteres fora de Base64 o login retorna 500
```

```powershell
cd APIDesbravadores
Copy-Item .env.example .env   # e preencha os valores
```

- `spring.jpa.hibernate.ddl-auto=none`: o schema vem **só** do repositório de banco. Antes de subir esta versão num banco existente, aplique as migrações 001 e 002 de lá.
- Nunca versione `.env`, senhas ou dumps.

## Como executar

```bash
cd APIDesbravadores
./mvnw.cmd spring-boot:run        # ou: ./mvnw.cmd -DskipTests package && java -jar target/APIDesbravadores-0.0.1-SNAPSHOT.jar
```

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- CORS liberado para `http://localhost:5173` e `5174` (Vite)
- Autenticação: o `POST /usuarios/login` devolve o JWT no cookie httpOnly `authToken`; o header `Authorization: Bearer` também é aceito

## Endpoints (70)

| Recurso | Endpoints |
|---|---|
| Usuários | `POST /usuarios/cadastro` · `POST /usuarios/login` · `POST /usuarios/logoff` · `GET/PUT /usuarios/perfil` · `PUT /usuarios/senha` · `GET /usuarios/painel-diretoria` |
| Unidades | `GET /unidades/diretor` · `GET /unidades/{idUnidade}/dashboard` · `GET /unidades/conselheiro` · `POST /unidades` · `PUT /unidades` · `DELETE /unidades/{idUnidade}` |
| Tarefas | `GET/POST /tarefas` · `GET/PUT/DELETE /tarefas/{id}` · `PATCH /tarefas/{id}/status` · `GET /tarefas/kanban` |
| Tarefas × Unidades | `GET /tarefas-unidades/{idTarefa}` · `PUT /tarefas-unidades/{idTarefa}/status` |
| Evidências | `GET/POST /evidencias` · `GET/PUT/DELETE /evidencias/{id}` |
| Convites | `GET/POST /convites` · `PUT/DELETE /convites/{id}` · `GET /convites/validar` |
| Notificações | `GET /notificacoes` · `PATCH /notificacoes/{id}/lida` · `PATCH /notificacoes/lidas` |
| Cadernos | `GET/POST /cadernos` · `GET/PUT/DELETE /cadernos/{id}` · `GET /cadernos/resumo` |
| Ciclos | `GET/POST /ciclos` · `GET/PUT/DELETE /ciclos/{id}` |
| Desbravadores | `GET/POST /desbravadores` · `GET/PUT/DELETE /desbravadores/{id}` |
| Execuções de caderno | `GET/POST /execucoes-caderno` · `GET/PUT/DELETE /execucoes-caderno/{id}` |
| Checklists de caderno | `GET/POST /checklists-caderno` · `GET/PUT/DELETE /checklists-caderno/{id}` |
| Clubes | `GET/POST /clubes` · `GET/PUT/DELETE /clubes/{id}` |
| Perfis | `GET/POST /perfis` · `GET/PUT/DELETE /perfis/{id}` |

As rotas da Diretoria exigem `hasRole('DIRETORIA')`. `GET /tarefas/kanban`, `PATCH /tarefas/{id}/status`, `PUT /tarefas-unidades/{id}/status` e `/unidades/conselheiro` exigem `hasRole('CONSELHEIRO')` e usam a unidade do usuário logado. O conselheiro não move tarefas para `CONCLUIDA`: isso acontece quando a Diretoria aprova a evidência.

## Testes

```bash
cd APIDesbravadores
./mvnw.cmd test
```

- `src/test/.../controller/`: testes de controller com MockMvc (services mockados).
- `src/test/.../service/IntegracaoFrontServiceTest.java`: regras dos contratos usados pelo front (convite, perfil/senha, distribuição de tarefas, decisão de evidência, pontos e notificação).

> Atenção: os testes de controller mockam o papel do usuário. Ao mudar autorização ou nomes de perfil, valide também contra o banco real, porque foi assim que o bug do `DIRETOR` × `Diretoria` passou despercebido.

## Próximos passos (backend)

1. **PR `consolidacao/sprint-final` → `main`**, depois do PR do banco (migrações 001 e 002).
2. **Decidir os nomes das tabelas:** padronizar `@Table(name = "...")` com a capitalização do schema, ou configurar o servidor para não diferenciar maiúsculas (necessário para deploy em Linux).
3. **Remover os `System.out.println` de depuração** (vazam o hash da senha e o token do convite no log) e usar um logger sem dados sensíveis.
4. **Fechar os `GET` anônimos** em `SecurityConfiguracao` (exigir autenticação e filtrar por clube).
5. **Conselheiro:** ~~Kanban da unidade~~ ✅. Faltam o check-in (Em andamento ↔ Em revisão automático) e o upload da evidência.
6. **Check-in:** implementar a transição automática para `EM REVISAO` quando todas as crianças elegíveis estiverem marcadas, e o retorno a `EM ANDAMENTO` ao desmarcar.
7. **Ciclo global:** um endpoint para aplicar o novo ciclo a todos os cadernos do clube (`Caderno.id_ciclo`), como no protótipo.
8. **Dashboard da unidade:** ✅ feito. Falta só o time confirmar a definição de "desconto" ou pedir uma tabela de penalidades.
9. **Upload de evidências e da foto de perfil** (armazenamento S3 ou local).
