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
@Table(name = "Checklist_Caderno")
@Getter
@Setter
@ToString
public class ChecklistCaderno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_checklist")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_execucao_caderno", nullable = false)
    private ExecucaoCaderno execucaoCaderno;

    @ManyToOne
    @JoinColumn(name = "id_desbravador", nullable = false)
    private Desbravador desbravador;

    @Column(name = "concluiu_tarefa", nullable = false)
    private Boolean concluiuTarefa = false;

    @Column(name = "data_marcacao")
    private LocalDateTime dataMarcacao;
}