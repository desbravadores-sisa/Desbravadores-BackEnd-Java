package school.sptech.APIDesbravadores.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ConviteRequestDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String tipoConta;

    @NotNull
    @Future
    private LocalDateTime dataExpiracao;

    private Integer idUnidade;

    @NotNull
    private Integer idClube;
}
