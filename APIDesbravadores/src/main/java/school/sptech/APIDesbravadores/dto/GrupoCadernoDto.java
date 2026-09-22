package school.sptech.APIDesbravadores.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class GrupoCadernoDto {
    private String nomeGrupo;
    private List<CadernoDetalhesDto> cadernos;

    public GrupoCadernoDto(String nomeGrupo, List<CadernoDetalhesDto> cadernos) {
        this.nomeGrupo = nomeGrupo;
        this.cadernos = cadernos;
    }

}
