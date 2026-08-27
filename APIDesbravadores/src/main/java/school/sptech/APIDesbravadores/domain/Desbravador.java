package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class Desbravador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_desbravador")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_clube")
    private Clube clube;

    @ManyToOne
    @JoinColumn(name = "id_unidade")
    private Unidade unidade;

    private String nome;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    private String genero;

    @Column(name = "data_admissao", insertable = false, updatable = false)
    private LocalDateTime dataAdmissao;

    private Boolean ativo;
}
