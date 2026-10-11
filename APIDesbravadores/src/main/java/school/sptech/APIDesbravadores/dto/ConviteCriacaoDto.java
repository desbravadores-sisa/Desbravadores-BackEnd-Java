package school.sptech.APIDesbravadores.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConviteCriacaoDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String tipoConta;

    private Integer idUnidade;
}
