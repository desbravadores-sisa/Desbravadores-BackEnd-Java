package school.sptech.APIDesbravadores.dto;

import jakarta.validation.constraints.Future;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
public class ConviteUpdateDto {

    private String statusConvite;

    @Future
    private LocalDateTime dataExpiracao;
}
