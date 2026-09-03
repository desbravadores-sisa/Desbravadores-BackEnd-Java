package school.sptech.APIDesbravadores.mapper;

import school.sptech.APIDesbravadores.domain.ChecklistCaderno;
import school.sptech.APIDesbravadores.domain.Desbravador;
import school.sptech.APIDesbravadores.domain.ExecucaoCaderno;
import school.sptech.APIDesbravadores.dto.ChecklistCadernoCriacaoDto;
import school.sptech.APIDesbravadores.dto.ChecklistCadernoResponseDto;

public class ChecklistCadernoMapper {

    public static ChecklistCaderno toEntity(ChecklistCadernoCriacaoDto dto, ExecucaoCaderno execucaoCaderno, Desbravador desbravador) {
        if (dto == null) return null;
        ChecklistCaderno entity = new ChecklistCaderno();
        entity.setExecucaoCaderno(execucaoCaderno);
        entity.setDesbravador(desbravador);
        entity.setConcluiuTarefa(dto.getConcluiuTarefa());
        return entity;
    }

    public static ChecklistCadernoResponseDto toResponseDto(ChecklistCaderno entity) {
        if (entity == null) return null;
        ChecklistCadernoResponseDto dto = new ChecklistCadernoResponseDto();
        dto.setId(entity.getId());
        dto.setIdExecucaoCaderno(entity.getExecucaoCaderno() != null ? entity.getExecucaoCaderno().getId() : null);
        dto.setIdDesbravador(entity.getDesbravador() != null ? entity.getDesbravador().getId() : null);
        dto.setConcluiuTarefa(entity.getConcluiuTarefa());
        dto.setDataMarcacao(entity.getDataMarcacao());
        return dto;
    }
}
