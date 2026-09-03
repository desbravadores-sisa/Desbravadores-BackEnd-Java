package school.sptech.APIDesbravadores.mapper;

import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.dto.ClubeAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.ClubeCriacaoDto;
import school.sptech.APIDesbravadores.dto.ClubeResponseDto;

import java.util.List;

public class ClubeMapper {

    public static ClubeResponseDto toResponse(Clube clube) {
        if (clube == null) {
            return null;
        }

        ClubeResponseDto dto = new ClubeResponseDto();
        dto.setId(clube.getId());
        dto.setNome(clube.getNome());
        dto.setRegiao(clube.getRegiao());
        dto.setCidade(clube.getCidade());
        dto.setDataCriacao(clube.getDataCriacao());

        return dto;
    }

    public static List<ClubeResponseDto> toResponse(List<Clube> clubes) {
        return clubes.stream()
                .map(ClubeMapper::toResponse)
                .toList();
    }

    public static Clube toEntity(ClubeCriacaoDto request) {
        if (request == null) {
            return null;
        }
        Clube clube = new Clube();
        clube.setNome(request.getNome());
        clube.setRegiao(request.getRegiao());
        clube.setCidade(request.getCidade());
        return clube;
    }

    public static void updateEntity(ClubeAtualizacaoDto request, Clube clube) {
        if (request == null || clube == null) {
            return;
        }
        clube.setNome(request.getNome());
        clube.setRegiao(request.getRegiao());
        clube.setCidade(request.getCidade());
    }
}
