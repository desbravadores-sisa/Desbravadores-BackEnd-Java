package school.sptech.APIDesbravadores.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import school.sptech.APIDesbravadores.domain.Unidade;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ConviteResponseDto {
    private String link;

    private Integer id;

    private String email;

    private String tipoConta;

    private LocalDateTime dataExpiracao;

    private String statusConvite;

    private String nomeUnidade;
}
