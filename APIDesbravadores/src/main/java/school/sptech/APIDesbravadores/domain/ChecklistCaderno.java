package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.*;
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
    @JoinColumn(name = "id_execucao_caderno")
    private ExecucaoCaderno execucaoCaderno;

    @ManyToOne
    @JoinColumn(name = "id_desbravador")
    private Desbravador desbravador;

    @Column(name = "concluiu_tarefa")
    private Boolean concluiuTarefa;

    @Column(name = "data_marcacao")
    private LocalDateTime dataMarcacao;
}
