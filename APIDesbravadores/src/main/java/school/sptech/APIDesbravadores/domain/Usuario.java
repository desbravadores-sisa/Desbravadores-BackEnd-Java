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

    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "senha", length = 256, nullable = false)
    private String senha;

    @Column(name = "ativo")
    private Boolean ativo;

    @ManyToOne
    @JoinColumn(name = "id_clube")
    private Clube clube;

    @ManyToOne
    @JoinColumn(name = "id_unidade")
    private Unidade unidade;

    @ManyToOne
    @JoinColumn(name = "id_perfil")
    private Perfil perfil;
}
