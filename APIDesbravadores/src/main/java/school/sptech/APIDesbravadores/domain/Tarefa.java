package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
@Table(name = "Tarefa")
@Getter
@Setter
@ToString
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarefa")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_clube", nullable = false)
    private Clube clube;

    @Column(name = "titulo", length = 150, nullable = false)
    private String nome;
    @Column(name = "request_id", length = 36, unique = true)
    private String requestId;

    private String descricao;

    @Column(name = "instrucoes_evidencia", columnDefinition = "TEXT")
    private String instrucoesEvidencia;

    @Column(name = "data_inicio")
    private java.time.LocalDate dataInicio;

    @Column(name = "tipo_tarefa", length = 20, nullable = false)
    private String tipoTarefa;

    private Integer pontuacao;

    @Convert(converter = LocalDateTimeToLocalDateConverter.class)
    @Column(name = "prazo_padrao")
    private LocalDateTime prazoEntrega;

    @ManyToOne
    @JoinColumn(name = "id_caderno")
    private Caderno caderno;

}
