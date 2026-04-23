package school.sptech.APIDesbravadores.mapper;

import org.apache.coyote.Request;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.dto.UnidadeResponseDto;

import java.util.List;

public class UnidadeMapper {

    public static UnidadeResponseDto toResponse(Unidade unidade){

        if (unidade == null){
            return null;
        }

        UnidadeResponseDto dto = new UnidadeResponseDto();
        /*
        * dto = {
        *  "id": null,
        *  "nome": null,
        *  "pontuacao": null
        * }
        * */
        dto.setNome(unidade.getNome());
        dto.setPontuacao(unidade.getPontuacao());
        dto.setId(unidade.getId());

        return dto;

    }


    public static List<UnidadeResponseDto> toResponse(List<Unidade> unidades){
        return unidades.stream().
                map(UnidadeMapper::toResponse).
                toList();
    }
}
