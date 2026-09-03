package school.sptech.APIDesbravadores.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class DesbravadorResponseDto {

    private Integer id;
    private Integer idClube;
    private Integer idUnidade;
    private String nome;
    private LocalDate dataNascimento;
    private String genero;
    private LocalDateTime dataAdmissao;
    private Boolean ativo;
}
