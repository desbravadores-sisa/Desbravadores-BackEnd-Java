package school.sptech.APIDesbravadores.mapper;

import school.sptech.APIDesbravadores.domain.Caderno;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.dto.CadernoCriacaoDto;
import school.sptech.APIDesbravadores.dto.CadernoResponseDto;

import java.util.List;

public class CadernoMapper {

    public static CadernoResponseDto toResponse(Caderno caderno) {
        if (caderno == null) {
            return null;
        }

        CadernoResponseDto dto = new CadernoResponseDto();
        dto.setId(caderno.getId());
        dto.setIdClube(caderno.getClube() != null ? caderno.getClube().getId() : null);
        dto.setNome(caderno.getNome());
        dto.setIdadeAlvo(caderno.getIdadeAlvo());

        return dto;
    }

    public static List<CadernoResponseDto> toResponse(List<Caderno> cadernos) {
        return cadernos.stream()
                .map(CadernoMapper::toResponse)
                .toList();
    }

    public static Caderno toEntity(CadernoCriacaoDto request, Clube clube) {
        if (request == null) {
            return null;
        }
        Caderno caderno = new Caderno();
        caderno.setNome(request.getNome());
        caderno.setIdadeAlvo(request.getIdadeAlvo());
        caderno.setClube(clube);
        return caderno;
    }
}
