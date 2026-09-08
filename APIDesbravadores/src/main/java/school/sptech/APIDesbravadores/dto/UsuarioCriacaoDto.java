package school.sptech.APIDesbravadores.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UsuarioCriacaoDto {

    @NotBlank
    @Size(max = 100)
    private String nome;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(max = 100)
    private String senha;

    @NotNull
    @Positive
    private Integer idPerfil;

    @NotNull
    @Positive
    private Integer idClube;
}
