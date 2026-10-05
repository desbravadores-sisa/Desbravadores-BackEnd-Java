package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Unidade_Tarefa")
@Getter
@Setter
@ToString
public class TarefaUnidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidade_tarefa")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_tarefa", nullable = false)
    private Tarefa tarefa;

    @ManyToOne
    @JoinColumn(name = "id_unidade", nullable = false)
    private Unidade unidade;

    @ManyToOne
    @JoinColumn(name = "id_ciclo", nullable = false)
    private Ciclo ciclo;

    @Column(name = "status_kanban")
    private StatusKanban statusKanban;

    @Column(name = "prazo_entrega")
    private java.time.LocalDateTime prazoEntrega;

    @Column(name = "data_conclusao")
    private java.time.LocalDateTime dataConclusao;

    @Column(name = "pontuacao_concedida")
    private Integer pontuacaoConcedida;

    @ManyToOne
    @JoinColumn(name = "id_revisor")
    private Usuario revisor;

}
