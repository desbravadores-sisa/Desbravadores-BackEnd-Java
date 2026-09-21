package school.sptech.APIDesbravadores.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class ConviteCriacaoDto {
    @NotBlank
    @Email
    private String email;

    @Future
    private LocalDate dataExpiracao;

    @NotNull
    @Positive
    private Integer idClube;

    private Integer idUnidade;
}
