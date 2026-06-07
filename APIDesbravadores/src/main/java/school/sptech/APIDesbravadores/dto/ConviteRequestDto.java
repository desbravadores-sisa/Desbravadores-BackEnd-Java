package school.sptech.APIDesbravadores.dto;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.Unidade;

import java.time.LocalDate;

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
    private LocalDate dataExpiracao;

    private Integer idUnidade;

    @NotNull
    private Integer idClube;
}
