# Integração de e-mails e notificações

A implementação usa os endpoints atuais de consolidacao/sprint-final, preservando o Kanban, o acompanhamento de cadernos, o perfil com foto e os dashboards da equipe.

Convites são enviados por SMTP com HTML e alternativa em texto. São permitidos apenas Diretoria e Conselheiro; conselheiro exige unidade do mesmo clube. Contas ativas e convites pendentes duplicados são rejeitados.

Novas tarefas notificam as unidades destinatárias. Evidências enviadas notificam a diretoria e publicam o evento de e-mail, processado após o commit. Falhas de SMTP são registradas e não desfazem a entrega. Não há reenvio automático persistente. Aprovações e correções continuam na implementação atual de NotificacaoService.

Preencha as variáveis do .env.example em um .env privado. O caminho de uploads usado pela consolidação é APP_UPLOADS_DIR. Nenhum segredo deve ser incluído no Git. O Mailpit opcional captura e-mails locais sem enviar a destinatários reais.

## Validação da integração

Front-end: build concluído e lint sem erros, com avisos preexistentes nas telas remotas.
Back-end: 139 testes executados; 135 passaram. Quatro falhas foram reproduzidas também na cópia isolada da branch remota original (136 testes): UnidadeControllerTest (mensagem de idade), dois casos de UnidadeServiceTest (validação de idade) e UsuarioControllerTest (endpoint de painel). Os testes de template e SMTP local passaram. Nenhum e-mail real foi enviado nesta integração.

As versões locais anteriores permanecem nas branches codex/backup-email-notifications-back e codex/backup-email-notifications-front. Os antigos endpoints paralelos /atividades e /entregas não substituem os contratos atuais /tarefas e /evidencias; as funcionalidades foram adaptadas aos serviços atuais.
