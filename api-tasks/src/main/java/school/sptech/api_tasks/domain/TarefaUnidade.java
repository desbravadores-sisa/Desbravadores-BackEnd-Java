package school.sptech.api_tasks.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "tarefa_unidade")
public class TarefaUnidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarefa_unidade")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "fk_tarefa")
    private Tarefa tarefa;

    @Column(name = "fk_unidade")
    private Integer fkUnidade;

    @Column(name = "status_kanban")
    private StatusKanban statusKanban;

    public TarefaUnidade() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Tarefa getTarefa() {
        return tarefa;
    }

    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
    }

    public Integer getFkUnidade() {
        return fkUnidade;
    }

    public void setFkUnidade(Integer fkUnidade) {
        this.fkUnidade = fkUnidade;
    }

    public StatusKanban getStatusKanban() {
        return statusKanban;
    }

    public void setStatusKanban(StatusKanban statusKanban) {
        this.statusKanban = statusKanban;
    }
}
