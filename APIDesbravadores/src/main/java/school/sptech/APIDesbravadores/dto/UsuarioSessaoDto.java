package school.sptech.APIDesbravadores.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UsuarioSessaoDto {
    private Integer idUsuario;
    private String nome;
    private String email;
    private String tipoConta;

    public UsuarioSessaoDto(Integer idUsuario, String nome, String email, String tipoConta) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.tipoConta = tipoConta;
    }
}
