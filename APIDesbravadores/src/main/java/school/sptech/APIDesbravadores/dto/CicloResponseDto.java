package school.sptech.APIDesbravadores.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class CicloResponseDto {

    private Integer id;
    private Integer idClube;
    private String nome;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Boolean ativo;
}
