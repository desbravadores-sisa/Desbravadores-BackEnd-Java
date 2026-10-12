package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** Vínculo explícito desbravador ↔ caderno (definido pela Diretoria em "Ver Caderno"). */
@Entity
@Table(name = "Desbravador_Caderno")
@Getter
@Setter
public class DesbravadorCaderno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_desbravador_caderno")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_desbravador")
    private Desbravador desbravador;

    @ManyToOne
    @JoinColumn(name = "id_caderno")
    private Caderno caderno;

    @Column(name = "data_vinculo")
    private LocalDateTime dataVinculo;

    /** Preenchida quando o desbravador cumpre todos os requisitos do caderno. */
    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;
}
