package school.sptech.APIDesbravadores.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import school.sptech.APIDesbravadores.domain.Unidade;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class ConviteResponseDto {

    private Integer id;

    private String email;

    private String tipoConta;

    private LocalDate dataExpiracao;

    private String statusConvite;

    private String nomeUnidade;
}
