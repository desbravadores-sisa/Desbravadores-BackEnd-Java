package school.sptech.APIDesbravadores.mapper;

import school.sptech.APIDesbravadores.domain.Perfil;
import school.sptech.APIDesbravadores.dto.PerfilAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.PerfilCriacaoDto;
import school.sptech.APIDesbravadores.dto.PerfilResponseDto;

import java.util.List;

public class PerfilMapper {

    public static PerfilResponseDto toResponse(Perfil perfil) {
        if (perfil == null) {
            return null;
        }

        PerfilResponseDto dto = new PerfilResponseDto();
        dto.setId(perfil.getId());
        dto.setNome(perfil.getNome());
        dto.setDescricao(perfil.getDescricao());

        return dto;
    }

    public static List<PerfilResponseDto> toResponse(List<Perfil> perfis) {
        return perfis.stream()
                .map(PerfilMapper::toResponse)
                .toList();
    }

    public static Perfil toEntity(PerfilCriacaoDto request) {
        if (request == null) {
            return null;
        }
        Perfil perfil = new Perfil();
        perfil.setNome(request.getNome());
        perfil.setDescricao(request.getDescricao());
        return perfil;
    }

    public static void updateEntity(PerfilAtualizacaoDto request, Perfil perfil) {
        if (request == null || perfil == null) {
            return;
        }
        perfil.setNome(request.getNome());
        perfil.setDescricao(request.getDescricao());
    }
}
