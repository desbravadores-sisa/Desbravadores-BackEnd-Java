package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Usuario")
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

    private String telefone;

    @Column(name = "foto_url")
    private String fotoUrl;

    private Boolean ativo;

    @ManyToOne
    @JoinColumn(name = "id_clube")
    private Clube clube;

    @ManyToOne
    @JoinColumn(name = "id_perfil")
    private Perfil perfil;

    @ManyToOne
    @JoinColumn(name = "id_unidade")
    private Unidade unidade;
}
