package school.sptech.APIDesbravadores.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Setter
@Getter
@ToString
public class ConviteUpdateDto {

    private String statusConvite;

    @Future
    private LocalDate dataExpiracao;
}
