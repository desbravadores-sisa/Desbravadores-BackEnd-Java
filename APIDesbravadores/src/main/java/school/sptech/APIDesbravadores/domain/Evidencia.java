package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class Evidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evidencia")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_unidade_tarefa")
    private TarefaUnidade unidadeTarefa;

    @Column(name = "url_s3")
    private String urlS3;

    @Column(name = "comentario_feedback")
    private String comentarioFeedback;

    @Column(name = "data_envio", insertable = false, updatable = false)
    private LocalDateTime dataEnvio;
}
