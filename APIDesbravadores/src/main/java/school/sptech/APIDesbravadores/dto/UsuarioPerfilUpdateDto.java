package school.sptech.APIDesbravadores.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioPerfilUpdateDto {

    @NotBlank
    private String nome;

    @NotBlank
    private String email;

    private String telefone;
}
