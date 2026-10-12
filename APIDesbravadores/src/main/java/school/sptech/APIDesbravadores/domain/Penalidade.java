package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** Desconto de pontos lançado pela Diretoria para uma unidade, dentro de um ciclo. */
@Entity
@Table(name = "Penalidade")
@Getter
@Setter
public class Penalidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_penalidade")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_unidade")
    private Unidade unidade;

    @ManyToOne
    @JoinColumn(name = "id_ciclo")
    private Ciclo ciclo;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private Integer pontos;

    private String motivo;

    @Column(name = "data_registro")
    private LocalDateTime dataRegistro;
}
