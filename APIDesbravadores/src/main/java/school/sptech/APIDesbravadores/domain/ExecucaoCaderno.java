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

import java.time.LocalDateTime;

@Entity
@Table(name = "Execucao_Caderno")
@Getter
@Setter
@ToString
public class ExecucaoCaderno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_execucao_caderno")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_unidade", nullable = false)
    private Unidade unidade;

    @ManyToOne
    @JoinColumn(name = "id_tarefa", nullable = false)
    private Tarefa tarefa;

    @ManyToOne
    @JoinColumn(name = "id_ciclo", nullable = false)
    private Ciclo ciclo;

    @Column(name = "status_kanban", length = 45)
    private StatusKanban statusKanban;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;
}