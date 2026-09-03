package school.sptech.APIDesbravadores.mapper;

import school.sptech.APIDesbravadores.domain.Ciclo;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.dto.CicloCriacaoDto;
import school.sptech.APIDesbravadores.dto.CicloResponseDto;

import java.util.List;

public class CicloMapper {

    public static CicloResponseDto toResponse(Ciclo ciclo) {
        if (ciclo == null) {
            return null;
        }

        CicloResponseDto dto = new CicloResponseDto();
        dto.setId(ciclo.getId());
        dto.setIdClube(ciclo.getClube() != null ? ciclo.getClube().getId() : null);
        dto.setNome(ciclo.getNome());
        dto.setDataInicio(ciclo.getDataInicio());
        dto.setDataFim(ciclo.getDataFim());
        dto.setAtivo(ciclo.getAtivo());

        return dto;
    }

    public static List<CicloResponseDto> toResponse(List<Ciclo> ciclos) {
        return ciclos.stream()
                .map(CicloMapper::toResponse)
                .toList();
    }

    public static Ciclo toEntity(CicloCriacaoDto request, Clube clube) {
        if (request == null) {
            return null;
        }
        Ciclo ciclo = new Ciclo();
        ciclo.setNome(request.getNome());
        ciclo.setDataInicio(request.getDataInicio());
        ciclo.setDataFim(request.getDataFim());
        ciclo.setAtivo(request.getAtivo());
        ciclo.setClube(clube);
        return ciclo;
    }
}
