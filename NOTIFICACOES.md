# Notificações — Tigre da Montanha

A API principal é `APIDesbravadores`, em Java 21 / Spring Boot 4.0.5. O módulo separado `api-tasks` é uma API antiga de tarefas sem o JWT e as relações atuais; este fluxo usa somente a API principal.

## Arquitetura e relações aproveitadas

- `Usuario` pertence a um clube, possui um `Perfil` e pode ter uma `Unidade`.
- `Perfil` é uma entidade, não um enum. As regras existentes usam `DIRETORIA` e `CONSELHEIRO`. Também são reconhecidos `DIRETOR`, `VICE-DIRETOR` / `VICE_DIRETOR` e `SECRETARIA`, quando esses perfis estiverem cadastrados. A seleção de convites usa os IDs e nomes retornados por `/perfil`; não insere novos perfis.
- `Tarefa` pertence ao clube e pode apontar para `Caderno`. `Unidade_Tarefa` associa tarefa, unidade e ciclo, mantendo o status de cada unidade.
- `Evidencia` pertence a `Unidade_Tarefa`. Uma unidade pode anexar várias evidências à mesma tarefa. A tela da Diretoria agrupa essas evidências por vínculo.
- `Convite` já tinha token aleatório, perfil, clube, unidade opcional e validade de 14 dias. Esse mecanismo foi preservado.
- `Notificacao` já existia como entidade, sem API. Foi completada com tipo e URL, e ganhou repository, serviço, DTO e controller.
- O JWT continua usando o cookie HttpOnly `authToken`, com suporte ao Bearer já existente. O usuário e seus vínculos são obtidos da sessão no backend.
- Não havia migrations versionadas nem pontuação implementada nos serviços atuais. A migração é um script SQL aditivo executado manualmente. Os novos pontos aprovados são registrados no vínculo, somados na resposta de unidades e exibidos no ranking da tela Unidades. Nomes de conselheiros e progresso também vêm da API.

Os endpoints de tarefas/Kanban/evidências estavam em grande parte comentados. As telas de convites, tarefas, aprovação e o sino usavam dados simulados. Esses pontos agora usam a API. O reconhecimento individual de cadernos continua fora deste fluxo: sua aba é preservada, sem registros simulados, e não ganhou novas regras de reconhecimento. Atividades de tipo CADERNO criadas em Gerenciar Tarefas usam as mesmas atribuições e notificações.

## Preparação e execução

1. Execute `src/main/resources/db/migrations/001_notificacoes.sql` no banco MySQL existente, em uma ferramenta que aceite `DELIMITER`. O script cria a fila de e-mails, completa a tabela existente de notificações, adiciona campos de auditoria/pontuação e índices. Não apaga tabelas nem registros. O script não é executado automaticamente ao iniciar.
2. Configure as variáveis do backend a partir de `.env.example`. O arquivo `.env` é lido quando o backend é iniciado no diretório `APIDesbravadores`.
3. O clube deve ter pelo menos uma unidade e um ciclo ativo. Na criação, selecione o ciclo; se a API receber `idCiclo` omitido, exige exatamente um ciclo ativo. Não são inventados ciclos ou unidades.
4. O backend mantém `spring.jpa.hibernate.ddl-auto=none`. Use Java 21 para executar `mvnw.cmd spring-boot:run` nesse diretório.
5. No frontend, em `desbravadores_aplicacao`, configure `VITE_API_URL`, execute `npm ci` e `npm run dev`. O endereço padrão da API é `http://localhost:8080`.
6. Para cookies locais, use o mesmo nome de host no frontend e no backend: `localhost:5173` e `localhost:8080`.

### Variáveis de ambiente

| Variável | Uso / padrão |
| --- | --- |
| DB_URL, DB_USER, DB_PASSWORD | Conexão MySQL já existente |
| JWT_SECRET | Chave existente; recomenda-se Base64 de pelo menos 32 bytes. O exemplo é exclusivamente local |
| MAIL_ENABLED | `false` por padrão. `true` habilita a fila SMTP |
| MAIL_HOST, MAIL_PORT | `localhost`, `1025` por padrão |
| MAIL_USERNAME, MAIL_PASSWORD | Credenciais SMTP, vazias por padrão |
| MAIL_FROM | Remetente, `noreply@tigredamontanha.local` por padrão |
| MAIL_AUTH | `false` local; configure `true` se o servidor exigir autenticação |
| MAIL_STARTTLS | `false` local; configure conforme o servidor SMTP |
| FRONTEND_URL | `http://localhost:5173`; usado nos convites e e-mails de aprovação |
| VITE_API_URL | Variável do frontend; `http://localhost:8080` |

Sem SMTP configurado, o backend inicia e os e-mails ficam pendentes no banco. Ao habilitar `MAIL_ENABLED`, a fila é processada a cada 15 segundos. Falhas têm até cinco tentativas, com espera de 2, 4, 6, 8 e 10 minutos. Após atingir o limite, corrija o SMTP e reprograme os registros falhos de forma administrativa. A fila oferece entrega com repetição em caso de falha; uma interrupção exatamente após o servidor SMTP aceitar uma mensagem pode exigir reconciliação.

## API

| Método | Endpoint | Resultado / autorização |
| --- | --- | --- |
| GET | /api/notifications?page=0&size=20 | Página do usuário autenticado, mais recentes primeiro; máximo 50 por página |
| GET | /api/notifications/unread-count | `{ "count": 3 }` do usuário autenticado |
| PATCH | /api/notifications/{id}/read | 204; 404 se não pertence ao usuário |
| PATCH | /api/notifications/read-all | 204; altera somente as notificações da sessão |
| PATCH | /evidencias/{id}/approve | Diretoria; corpo `{ "pontuacao": 300 }`, ou `{}` para usar a pontuação da tarefa |
| PATCH | /evidencias/{id}/correction | Diretoria; corpo `{ "justificativa": "Documento ausente" }` |
| GET | /tarefas/opcoes | Diretoria; unidades, ciclos ativos e cadernos do clube |
| GET | /usuarios/buscarUsuario | Sessão atual sem expor o JWT |

Foram completadas as rotas existentes `GET/POST /tarefas`, `GET/PUT/DELETE /tarefas/{id}`, `GET /tarefas/kanban`, `PATCH /tarefas/{id}/status`, `GET /tarefas-unidades/{idTarefa}`, `PUT /tarefas-unidades/{idTarefa}/status` e o CRUD de evidências. As rotas de tarefas agora exigem autenticação.

Exemplo de criação:

```json
{
  "nome": "Campori Regional - Preparação",
  "descricao": "Organizar documentos e equipamentos",
  "tipoTarefa": "GERAL",
  "pontuacao": 300,
  "unidadeIds": [2],
  "todasUnidades": false,
  "idCiclo": 1,
  "prazoEntrega": "2026-11-01T23:59:59",
  "requestId": "01234567-89ab-cdef-0123-456789abcdef"
}
```

Para todas as unidades, use `todasUnidades: true`. Para atividade de caderno, use `tipoTarefa: "CADERNO"` e `idCaderno`. O frontend envia um `requestId` estável por criação; repetir a mesma chave retorna a mesma tarefa e não repete as notificações. Clientes adicionais devem enviar esse campo para proteger retries da criação.

A edição com `unidadeIds` acrescenta novas atribuições, mantendo as existentes e suas evidências. Ela não exclui vínculos. A edição do título, descrição ou pontos não gera notificações.

## Eventos implementados

| Evento de negócio | Destinatários | Tipo / canal |
| --- | --- | --- |
| Criar convite | E-mail informado | E-mail com função, unidade, link e validade |
| Criar tarefa ou acrescentar atribuições | Conselheiros ativos das unidades novas, uma vez por usuário | NOVA_TAREFA |
| Transição real para Em Revisão | Diretoria ativa do mesmo clube | TAREFA_EM_REVISAO |
| Aprovar evidência/tarefa | Conselheiros ativos da unidade | EVIDENCIA_APROVADA + e-mail |
| Solicitar correção | Conselheiros ativos da unidade | CORRECAO_SOLICITADA com justificativa |

As notificações são persistidas dentro da transação de negócio. O React não possui endpoint de criação de notificações. Revisão e aprovação bloqueiam o mesmo vínculo no banco, inclusive quando são analisadas duas evidências de uma mesma tarefa. Repetir o status, a aprovação ou a mesma correção não repete os efeitos. Uma nova revisão após uma correção é um novo evento legítimo.

O Conselheiro não pode concluir uma tarefa diretamente nem alterar evidências durante a revisão ou depois da conclusão. A aprovação registra revisor, data e pontos por unidade. A correção retorna a tarefa a Em Andamento e registra revisor, data e justificativa.

## Teste manual de cada fluxo

1. **Convite:** entre como Diretoria, abra Convites & Usuários, selecione um perfil existente e, para Conselheiro, uma unidade. Gere o convite. Confira o link no resultado e o e-mail no SMTP configurado. Abra o link e conclua o cadastro com o mesmo e-mail. Reutilização e expiração seguem as validações existentes.
2. **Uma unidade:** crie uma tarefa somente para Tigresas. Entre como o conselheiro de Tigresas e confira o badge e a mensagem. Entre como conselheiro de outra unidade: a tarefa e a notificação não devem aparecer.
3. **Todas as unidades:** crie uma tarefa com Todas as unidades. Cada conselheiro ativo deve receber uma notificação.
4. **Edição:** altere apenas título/descrição. Nenhuma nova notificação deve surgir. Acrescente uma nova unidade: somente seus conselheiros recebem.
5. **Revisão:** em Minhas Tarefas, mova para Em Andamento, anexe uma evidência e arraste ou selecione Em Revisão. A Diretoria recebe a notificação e o link abre Evidências e Reconhecimentos na tarefa correspondente. Repetir o mesmo status não incrementa o contador.
6. **Aprovação:** como Diretoria, abra a evidência e confirme os pontos. O Conselheiro recebe a notificação, a tarefa aparece em Concluído, os pontos são somados na resposta da unidade e um e-mail entra na fila. Repita a chamada de aprovação: não devem surgir novos pontos ou e-mails.
7. **Correção:** em uma entrega diferente, solicite correção e informe o motivo. O Conselheiro recebe a notificação e vê o motivo na tarefa/evidência. Corrija o arquivo e envie novamente para revisão.
8. **Leitura:** abra o sino, marque uma notificação como lida e confira o badge. Marque todas e atualize a página: a leitura deve continuar salva. Notificações lidas e não lidas têm aparência diferente; o painel permite carregar mais.
9. **Permissões:** como Conselheiro, tente acessar Convites ou Evidências da Diretoria. Pela API, teste a leitura de um ID de notificação de outro usuário: deve retornar 404. Rotas sem cookie/token retornam 401; ações de Diretoria com sessão de Conselheiro retornam 403.
10. **Falha de SMTP:** deixe o envio desabilitado ou o SMTP indisponível. Convites e aprovações continuam gravados; a fila mantém as mensagens. Configure o SMTP, habilite o envio e confira a entrega.

O anexo utiliza o modelo de URL que já existia na entidade `Evidencia`. Não foi criado um armazenamento de arquivos ou upload para S3.

## Validação automatizada

- `mvnw.cmd test`: suíte de controllers, serviços e integração com H2; servidor Spring real, permissões, cookie JWT, destinatários, contagem, leitura, convites, SMTP simulado e chamadas concorrentes.
- `npm run build`: compilação de produção do React.
- `npm run lint`: análise do frontend.
- O servidor Vite foi iniciado e respondeu HTTP 200.
- A migração MySQL não foi aplicada ao banco do usuário, pois não há conexão configurada no projeto. Os testes usam um banco isolado.

## Arquivos criados e alterados

As listas abaixo usam caminhos relativos à raiz de cada projeto.

### Backend — criados

```text
APIDesbravadores/src/main/java/school/sptech/APIDesbravadores/
  config/EmailConfig.java
  controller/NotificacaoController.java
  controller/TarefaOpcoesController.java
  domain/EmailPendente.java
  dto/AprovacaoRequestDto.java
  dto/CorrecaoRequestDto.java
  dto/NotificacaoResponseDto.java
  exception/FluxoExceptionHandler.java
  repository/CadernoRepository.java
  repository/CicloRepository.java
  repository/EmailPendenteRepository.java
  repository/NotificacaoRepository.java
  service/AcessoService.java
  service/EmailService.java
  service/NotificacaoService.java
APIDesbravadores/src/main/resources/db/migrations/001_notificacoes.sql
APIDesbravadores/src/test/java/school/sptech/APIDesbravadores/service/EmailServiceTest.java
APIDesbravadores/src/test/java/school/sptech/APIDesbravadores/service/NotificacaoFluxoTest.java
APIDesbravadores/src/test/resources/application.properties
NOTIFICACOES.md
```

### Backend — alterados

```text
APIDesbravadores/.env.example
APIDesbravadores/pom.xml
APIDesbravadores/src/main/resources/application.properties
APIDesbravadores/src/main/java/school/sptech/APIDesbravadores/
  config/AutenticacaoFilter.java
  config/AutenticacaoProvider.java
  config/SecurityConfiguracao.java
  controller/ConviteController.java
  controller/EvidenciaController.java
  controller/TarefaController.java
  controller/TarefaUnidadeController.java
  controller/UnidadeController.java
  controller/UsuarioController.java
  domain/Evidencia.java
  domain/Notificacao.java
  domain/StatusKanban.java
  domain/Tarefa.java
  domain/TarefaUnidade.java
  dto/ConviteResponseDto.java
  dto/EvidenciaResponseDto.java
  dto/TarefaCreateDto.java
  dto/TarefaResponseDto.java
  dto/TarefaUpdateDto.java
  dto/UsuarioDetalhesDto.java
  dto/UnidadeCriacaoDto.java
  dto/UnidadeResponseDto.java
  mapper/EvidenciaMapper.java
  mapper/TarefaMapper.java
  mapper/UnidadeMapper.java
  repository/ClubeRepository.java
  repository/EvidenciaRepository.java
  repository/TarefaRepository.java
  repository/TarefaUnidadeRepository.java
  service/AutenticacaoService.java
  service/ConviteService.java
  service/EvidenciaService.java
  service/TarefaService.java
  service/UnidadeService.java
  service/UsuarioService.java
APIDesbravadores/src/test/java/school/sptech/APIDesbravadores/
  controller/ConviteControllerTest.java
  controller/EvidenciaControllerTest.java
  controller/UnidadeControllerTest.java
  controller/UsuarioControllerTest.java
  service/EvidenciaServiceTest.java
```

### Frontend — criados

```text
desbravadores_aplicacao/.env.example
desbravadores_aplicacao/src/pages/MinhasTarefas.jsx
desbravadores_aplicacao/src/pages/MinhasTarefas.module.css
desbravadores_aplicacao/src/service/SessionProvider.jsx
desbravadores_aplicacao/src/service/session.js
desbravadores_aplicacao/src/service/feedback.js
```

### Frontend — alterados

```text
desbravadores_aplicacao/src/App.jsx
desbravadores_aplicacao/src/components/AuthFrom/AuthForm.jsx
desbravadores_aplicacao/src/components/Navbar/Navbar.jsx
desbravadores_aplicacao/src/components/Navbar/Navbar.module.css
desbravadores_aplicacao/src/pages/Convites.jsx
desbravadores_aplicacao/src/pages/Evidencias.jsx
desbravadores_aplicacao/src/pages/Tarefas.jsx
desbravadores_aplicacao/src/pages/Tarefas.module.css
desbravadores_aplicacao/src/pages/Unidades.jsx
desbravadores_aplicacao/src/service/api.js
```
