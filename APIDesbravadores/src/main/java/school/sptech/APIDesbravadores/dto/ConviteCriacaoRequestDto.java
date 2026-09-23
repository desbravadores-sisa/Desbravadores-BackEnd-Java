package school.sptech.APIDesbravadores.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class ConviteCriacaoRequestDto {

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    private String email;

    @NotNull(message = "O perfil (Diretoria ou Conselheiro) é obrigatório")
    private Integer idPerfil;

    private Integer idUnidade;
}
