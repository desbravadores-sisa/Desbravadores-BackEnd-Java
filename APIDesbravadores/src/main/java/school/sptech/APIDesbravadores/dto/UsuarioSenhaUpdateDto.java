package school.sptech.APIDesbravadores.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioSenhaUpdateDto {

    @NotBlank
    private String senhaAtual;

    @NotBlank
    @Size(min = 6)
    private String novaSenha;
}
