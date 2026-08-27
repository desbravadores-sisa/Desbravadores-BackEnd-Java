package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Caderno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_caderno")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_clube")
    private Clube clube;

    private String nome;

    @Column(name = "idade_alvo")
    private Integer idadeAlvo;
}
