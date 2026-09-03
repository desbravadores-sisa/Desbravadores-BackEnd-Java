package school.sptech.APIDesbravadores.mapper;

import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.Desbravador;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.dto.DesbravadorCriacaoDto;
import school.sptech.APIDesbravadores.dto.DesbravadorResponseDto;

import java.util.List;

public class DesbravadorMapper {

    public static DesbravadorResponseDto toResponse(Desbravador desbravador) {
        if (desbravador == null) {
            return null;
        }

        DesbravadorResponseDto dto = new DesbravadorResponseDto();
        dto.setId(desbravador.getId());
        dto.setIdClube(desbravador.getClube() != null ? desbravador.getClube().getId() : null);
        dto.setIdUnidade(desbravador.getUnidade() != null ? desbravador.getUnidade().getId() : null);
        dto.setNome(desbravador.getNome());
        dto.setDataNascimento(desbravador.getDataNascimento());
        dto.setGenero(desbravador.getGenero());
        dto.setDataAdmissao(desbravador.getDataAdmissao());
        dto.setAtivo(desbravador.getAtivo());

        return dto;
    }

    public static List<DesbravadorResponseDto> toResponse(List<Desbravador> desbravadores) {
        return desbravadores.stream()
                .map(DesbravadorMapper::toResponse)
                .toList();
    }

    public static Desbravador toEntity(DesbravadorCriacaoDto request, Clube clube, Unidade unidade) {
        if (request == null) {
            return null;
        }
        Desbravador desbravador = new Desbravador();
        desbravador.setNome(request.getNome());
        desbravador.setDataNascimento(request.getDataNascimento());
        desbravador.setGenero(request.getGenero());
        desbravador.setAtivo(request.getAtivo());
        desbravador.setClube(clube);
        desbravador.setUnidade(unidade);
        return desbravador;
    }
}
