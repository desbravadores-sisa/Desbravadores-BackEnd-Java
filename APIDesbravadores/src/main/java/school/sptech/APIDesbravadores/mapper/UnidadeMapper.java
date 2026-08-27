package school.sptech.APIDesbravadores.mapper;

import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.dto.UnidadeCriacaoDto;
import school.sptech.APIDesbravadores.dto.UnidadeResponseDto;

import java.util.List;

public class UnidadeMapper {

    public static UnidadeResponseDto toResponse(Unidade unidade){

        if (unidade == null){
            return null;
        }

        UnidadeResponseDto dto = new UnidadeResponseDto();
        dto.setId(unidade.getId());
        dto.setNome(unidade.getNome());
        dto.setGenero(unidade.getGenero());
        dto.setIdadeMinima(unidade.getIdadeMinima());
        dto.setIdadeMaxima(unidade.getIdadeMaxima());

        return dto;

    }

    public static List<UnidadeResponseDto> toResponse(List<Unidade> unidades){
        return unidades.stream().
                map(UnidadeMapper::toResponse).
                toList();
    }

    public static Unidade toEntity(UnidadeCriacaoDto request, Clube clube){
        if (request == null){
            return null;
        }
        Unidade unidade = new Unidade();
        unidade.setNome(request.getNome());
        unidade.setGenero(request.getGenero());
        unidade.setIdadeMinima(request.getIdadeMinima());
        unidade.setIdadeMaxima(request.getIdadeMaxima());
        unidade.setClube(clube);
        return unidade;
    }
}
