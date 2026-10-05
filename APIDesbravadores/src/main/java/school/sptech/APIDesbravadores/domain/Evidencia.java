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
@Table(name = "Evidencia")
@Getter
@Setter
@ToString
public class Evidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evidencia")
    private Integer id;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "id_tarefa_unidade")
    private TarefaUnidade tarefaUnidade;

    @Column(name = "nome")
    private String nome;

    @Column(name = "url_s3", length = 500, nullable = false)
    private String urlAnexo;

    @Column(name = "comentario_feedback", columnDefinition = "TEXT")
    private String comentarioFeedback;

    @ManyToOne
    @JoinColumn(name = "id_revisor")
    private Usuario revisor;

    @Column(name = "data_analise")
    private LocalDateTime dataAnalise;

    @Column(name = "data_envio", insertable = false, updatable = false)
    private LocalDateTime dataUpload;

}
