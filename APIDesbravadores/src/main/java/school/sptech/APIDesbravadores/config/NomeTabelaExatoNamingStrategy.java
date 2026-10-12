package school.sptech.APIDesbravadores.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * Mantém o nome das TABELAS exatamente como em {@code @Table}/{@code @Entity}
 * (ex.: "Unidade_Tarefa", "Usuario"), igual ao script do banco. Colunas continuam em snake_case.
 *
 * Motivo: no MySQL em Linux os nomes de tabela diferenciam maiúsculas/minúsculas
 * (lower_case_table_names=0). A estratégia padrão do Spring/Hibernate converte tudo para
 * minúsculas ("unidade_tarefa"), o que só funciona em Windows/macOS ou com
 * lower_case_table_names=1.
 */
public class NomeTabelaExatoNamingStrategy extends PhysicalNamingStrategySnakeCaseImpl {

    @Override
    public Identifier toPhysicalTableName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return logicalName;
    }
}
