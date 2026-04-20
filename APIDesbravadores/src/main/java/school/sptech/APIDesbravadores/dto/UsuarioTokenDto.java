package school.sptech.APIDesbravadores.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UsuarioTokenDto {

    private Integer idUsuario;

    private String nome;

    private String email;

    private String tipoConta;

    private String token;

    public UsuarioTokenDto(Integer idUsuario, String nome, String email, String tipoConta, String token) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.tipoConta = tipoConta;
        this.token = token;
    }
}
