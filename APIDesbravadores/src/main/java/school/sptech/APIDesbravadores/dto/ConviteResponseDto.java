package school.sptech.APIDesbravadores.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ConviteResponseDto {

    private Integer id;

    private String email;

    private String tipoConta;

    private LocalDateTime dataExpiracao;

    private String statusConvite;

    private String nomeUnidade;
}
