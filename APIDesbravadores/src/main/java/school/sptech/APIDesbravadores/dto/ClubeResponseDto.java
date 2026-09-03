package school.sptech.APIDesbravadores.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ClubeResponseDto {

    private Integer id;

    private String nome;

    private String regiao;

    private String cidade;

    private LocalDateTime dataCriacao;
}
