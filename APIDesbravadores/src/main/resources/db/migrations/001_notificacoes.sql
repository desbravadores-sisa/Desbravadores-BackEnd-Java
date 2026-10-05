-- MySQL 8. Execute uma vez no banco existente, antes de iniciar a nova versão.
-- O projeto usa ddl-auto=none e não possui Flyway/Liquibase. Não cria nem remove dados do clube.
CREATE TABLE IF NOT EXISTS Notificacao (
    id_notificacao INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_clube INT NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    mensagem TEXT NOT NULL,
    tipo_referencia VARCHAR(45) NULL,
    id_referencia INT NULL,
    lida BOOLEAN NOT NULL DEFAULT FALSE,
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notificacao_usuario FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario),
    CONSTRAINT fk_notificacao_clube FOREIGN KEY (id_clube) REFERENCES Clube(id_clube)
);
CREATE TABLE IF NOT EXISTS Email_Pendente (
    id_email INT AUTO_INCREMENT PRIMARY KEY,
    destinatario VARCHAR(255) NOT NULL,
    assunto VARCHAR(255) NOT NULL,
    conteudo TEXT NOT NULL,
    enviado BOOLEAN NOT NULL DEFAULT FALSE,
    tentativas INT NOT NULL DEFAULT 0,
    proxima_tentativa DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email_pendente (enviado, tentativas, proxima_tentativa)
);

-- Amplia os textos existentes para comportar justificativas sem truncar dados.
ALTER TABLE Notificacao MODIFY COLUMN mensagem TEXT NOT NULL;
ALTER TABLE Evidencia MODIFY COLUMN comentario_feedback TEXT NULL;

DELIMITER //
CREATE PROCEDURE tigre_notificacoes_adicionar_coluna(IN tabela VARCHAR(64), IN coluna VARCHAR(64), IN definicao VARCHAR(255))
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = DATABASE() AND table_name = tabela AND column_name = coluna
    ) THEN
        SET @tigre_sql = CONCAT('ALTER TABLE `', tabela, '` ADD COLUMN `', coluna, '` ', definicao);
        PREPARE tigre_stmt FROM @tigre_sql;
        EXECUTE tigre_stmt;
        DEALLOCATE PREPARE tigre_stmt;
    END IF;
END//
CREATE PROCEDURE tigre_notificacoes_adicionar_indice(IN tabela VARCHAR(64), IN indice VARCHAR(64), IN definicao VARCHAR(255))
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.statistics
        WHERE table_schema = DATABASE() AND table_name = tabela AND index_name = indice
    ) THEN
        SET @tigre_sql = CONCAT('ALTER TABLE `', tabela, '` ADD ', definicao);
        PREPARE tigre_stmt FROM @tigre_sql;
        EXECUTE tigre_stmt;
        DEALLOCATE PREPARE tigre_stmt;
    END IF;
END//
DELIMITER ;

CALL tigre_notificacoes_adicionar_coluna('Notificacao', 'tipo', 'VARCHAR(45) NULL');
CALL tigre_notificacoes_adicionar_coluna('Notificacao', 'url_destino', 'VARCHAR(500) NULL');
CALL tigre_notificacoes_adicionar_coluna('Tarefa', 'request_id', 'VARCHAR(36) NULL');
CALL tigre_notificacoes_adicionar_coluna('Tarefa', 'instrucoes_evidencia', 'TEXT NULL');
CALL tigre_notificacoes_adicionar_coluna('Tarefa', 'data_inicio', 'DATE NULL');
CALL tigre_notificacoes_adicionar_coluna('Unidade_Tarefa', 'pontuacao_concedida', 'INT NULL');
CALL tigre_notificacoes_adicionar_coluna('Unidade_Tarefa', 'id_revisor', 'INT NULL REFERENCES Usuario(id_usuario)');
CALL tigre_notificacoes_adicionar_coluna('Evidencia', 'id_revisor', 'INT NULL REFERENCES Usuario(id_usuario)');
CALL tigre_notificacoes_adicionar_coluna('Evidencia', 'data_analise', 'DATETIME NULL');

CALL tigre_notificacoes_adicionar_indice('Notificacao', 'idx_notificacao_usuario_data', 'INDEX idx_notificacao_usuario_data (id_usuario, data_criacao, id_notificacao)');
CALL tigre_notificacoes_adicionar_indice('Notificacao', 'idx_notificacao_usuario_lida', 'INDEX idx_notificacao_usuario_lida (id_usuario, lida)');
CALL tigre_notificacoes_adicionar_indice('Tarefa', 'uk_tarefa_request', 'UNIQUE INDEX uk_tarefa_request (request_id)');

DROP PROCEDURE tigre_notificacoes_adicionar_coluna;
DROP PROCEDURE tigre_notificacoes_adicionar_indice;
