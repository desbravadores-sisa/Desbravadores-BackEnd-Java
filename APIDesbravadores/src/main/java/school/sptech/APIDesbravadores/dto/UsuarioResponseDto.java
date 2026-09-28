package school.sptech.APIDesbravadores.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UsuarioResponseDto {
    private Integer id;
    private String nome;
    private String email;
    private String nomeClube;
    private String nomeUnidade;
    private String nomePerfil;
}
