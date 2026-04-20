package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    private String nome;

    private String email;

    private String senha;

    private String tipoConta;

    @ManyToOne
    @JoinColumn(name = "id_clube")
    private Clube clube;

    @ManyToOne
    @JoinColumn(name = "id_unidade")
    private Unidade unidade;
}
