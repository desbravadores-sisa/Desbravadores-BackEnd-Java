package school.sptech.APIDesbravadores.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CadernoResponseDto {

    private Integer id;
    private Integer idClube;
    private String nome;
    private Integer idadeAlvo;
}
