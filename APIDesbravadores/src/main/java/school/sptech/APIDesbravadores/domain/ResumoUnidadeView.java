package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "vw_resumo_unidades")
@Immutable
@Getter
@ToString
public class ResumoUnidadeView {

    @Id
    @Column(name = "id_unidade")
    private Integer idUnidade;

    @Column(name = "id_clube")
    private Integer idClube;

    @Column(name = "nome_unidade")
    private String nomeUnidade;

    @Column(name = "nome_conselheiro")
    private String nomeConselheiro;

    @Column(name = "pontuacao")
    private Integer pontuacao;

    @Column(name = "total_tarefas")
    private Integer totalTarefas;

    @Column(name = "tarefas_concluidas")
    private Integer tarefasConcluidas;
}
