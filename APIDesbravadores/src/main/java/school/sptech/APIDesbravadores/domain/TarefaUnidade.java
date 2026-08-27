package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Unidade_Tarefa")
public class TarefaUnidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidade_tarefa")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_unidade")
    private Unidade unidade;

    @ManyToOne
    @JoinColumn(name = "id_tarefa")
    private Tarefa tarefa;

    @ManyToOne
    @JoinColumn(name = "id_ciclo")
    private Ciclo ciclo;

    @Column(name = "status_kanban")
    private StatusKanban statusKanban;

    @Column(name = "prazo_entrega")
    private LocalDateTime prazoEntrega;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    public TarefaUnidade() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
    }

    public Tarefa getTarefa() {
        return tarefa;
    }

    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
    }

    public Ciclo getCiclo() {
        return ciclo;
    }

    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }

    public StatusKanban getStatusKanban() {
        return statusKanban;
    }

    public void setStatusKanban(StatusKanban statusKanban) {
        this.statusKanban = statusKanban;
    }

    public LocalDateTime getPrazoEntrega() {
        return prazoEntrega;
    }

    public void setPrazoEntrega(LocalDateTime prazoEntrega) {
        this.prazoEntrega = prazoEntrega;
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(LocalDateTime dataConclusao) {
        this.dataConclusao = dataConclusao;
    }
}
