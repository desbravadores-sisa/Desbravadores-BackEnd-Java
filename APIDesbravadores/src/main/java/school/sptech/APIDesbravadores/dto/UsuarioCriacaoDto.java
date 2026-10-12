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

    @Size(max = 45)
    private String tipoConta;

    @Positive
    private Integer idClube;

    /** Token do convite. Quando presente, perfil/clube/unidade vêm do convite. */
    @Size(max = 128)
    private String token;
}
