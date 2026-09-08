package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Unidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidade")
    private Integer id;

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "genero", length = 45)
    private String genero;

    @Column(name = "idade_minima")
    private Integer idadeMinima;

    @Column(name = "idade_maxima")
    private Integer idadeMaxima;

    @ManyToOne
    @JoinColumn(name = "id_clube")
    private Clube clube;

}
