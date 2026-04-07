CREATE SCHEMA IF NOT EXISTS clube_desbravadores
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_0900_ai_ci;

USE clube_desbravadores;


-- =========================
-- CLUBE
-- =========================
CREATE TABLE clube (
    id_clube INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    regiao VARCHAR(100) DEFAULT NULL,
    cidade VARCHAR(100) DEFAULT NULL,

    PRIMARY KEY (id_clube)
) ENGINE = InnoDB;


-- =========================
-- UNIDADE
-- =========================
CREATE TABLE unidade (
    id_unidade INT NOT NULL AUTO_INCREMENT,
    fk_clube INT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    pontuacao INT DEFAULT NULL,

    PRIMARY KEY (id_unidade, fk_clube),

    CONSTRAINT fk_unidade_clube
        FOREIGN KEY (fk_clube)
        REFERENCES clube (id_clube)
) ENGINE = InnoDB;


-- =========================
-- CONVITE
-- =========================
CREATE TABLE convite (
    id_convite INT NOT NULL AUTO_INCREMENT,
    fk_clube INT NOT NULL,
    fk_unidade INT DEFAULT NULL,
    email VARCHAR(100) NOT NULL,
    token VARCHAR(64) NOT NULL,
    tipo_conta ENUM('Diretor', 'Secretaria', 'Conselheiro') NOT NULL,
    data_expiracao DATETIME NOT NULL,
    status VARCHAR(100) DEFAULT 'Pendente',

    PRIMARY KEY (id_convite, fk_clube),
    UNIQUE (token),

    CONSTRAINT fk_convite_clube
        FOREIGN KEY (fk_clube)
        REFERENCES clube (id_clube),

    CONSTRAINT fk_convite_unidade
        FOREIGN KEY (fk_unidade)
        REFERENCES unidade (id_unidade)
) ENGINE = InnoDB;


-- =========================
-- TAREFA
-- =========================
CREATE TABLE tarefa (
    id_tarefa INT NOT NULL AUTO_INCREMENT,
    fk_clube INT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT DEFAULT NULL,
    pontuacao INT DEFAULT 0,
    prazo_entrega DATETIME DEFAULT NULL,
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id_tarefa, fk_clube),

    CONSTRAINT fk_tarefa_clube
        FOREIGN KEY (fk_clube)
        REFERENCES clube (id_clube)
) ENGINE = InnoDB;


-- =========================
-- TAREFA UNIDADE
-- =========================
CREATE TABLE tarefa_unidade (
    id_tarefa_unidade INT NOT NULL AUTO_INCREMENT,
    fk_tarefa INT NOT NULL,
    fk_unidade INT NOT NULL,
    status_kanban ENUM('A fazer', 'Em andamento', 'Em revisão', 'Concluído') DEFAULT 'A fazer',

    PRIMARY KEY (id_tarefa_unidade, fk_tarefa, fk_unidade),

    CONSTRAINT fk_tarefa_unidade_tarefa
        FOREIGN KEY (fk_tarefa)
        REFERENCES tarefa (id_tarefa),

    CONSTRAINT fk_tarefa_unidade_unidade
        FOREIGN KEY (fk_unidade)
        REFERENCES unidade (id_unidade)
) ENGINE = InnoDB;


-- =========================
-- EVIDENCIA
-- =========================
CREATE TABLE evidencia (
    id_evidencia INT NOT NULL AUTO_INCREMENT,
    fk_tarefa_unidade INT NOT NULL,
    nome VARCHAR(100),
    url_s3 VARCHAR(255) NOT NULL,
    data_upload TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id_evidencia, fk_tarefa_unidade),

    CONSTRAINT fk_evidencia_tarefa_unidade
        FOREIGN KEY (fk_tarefa_unidade)
        REFERENCES tarefa_unidade (id_tarefa_unidade)
) ENGINE = InnoDB;


-- =========================
-- USUARIO
-- =========================
CREATE TABLE usuario (
    id_usuario INT NOT NULL AUTO_INCREMENT,
    fk_clube INT NOT NULL,
    fk_unidade INT DEFAULT NULL,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    senha VARCHAR(256) NOT NULL,
    tipo_conta ENUM('Diretor', 'Conselheiro') NOT NULL,

    PRIMARY KEY (id_usuario, fk_clube),
    UNIQUE (email),

    CONSTRAINT fk_usuario_clube
        FOREIGN KEY (fk_clube)
        REFERENCES clube (id_clube),

    CONSTRAINT fk_usuario_unidade
        FOREIGN KEY (fk_unidade)
        REFERENCES unidade (id_unidade)
) ENGINE = InnoDB;