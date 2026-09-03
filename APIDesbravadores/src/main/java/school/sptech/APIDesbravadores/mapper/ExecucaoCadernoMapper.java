package school.sptech.APIDesbravadores.mapper;

import school.sptech.APIDesbravadores.domain.Ciclo;
import school.sptech.APIDesbravadores.domain.ExecucaoCaderno;
import school.sptech.APIDesbravadores.domain.StatusKanban;
import school.sptech.APIDesbravadores.domain.Tarefa;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.dto.ExecucaoCadernoCriacaoDto;
import school.sptech.APIDesbravadores.dto.ExecucaoCadernoResponseDto;

public class ExecucaoCadernoMapper {

    public static ExecucaoCaderno toEntity(ExecucaoCadernoCriacaoDto dto, Unidade unidade, Tarefa tarefa, Ciclo ciclo) {
        if (dto == null) return null;
        ExecucaoCaderno entity = new ExecucaoCaderno();
        entity.setUnidade(unidade);
        entity.setTarefa(tarefa);
        entity.setCiclo(ciclo);
        entity.setStatusKanban(StatusKanban.fromString(dto.getStatusKanban()));
        return entity;
    }

    public static ExecucaoCadernoResponseDto toResponseDto(ExecucaoCaderno entity) {
        if (entity == null) return null;
        ExecucaoCadernoResponseDto dto = new ExecucaoCadernoResponseDto();
        dto.setId(entity.getId());
        dto.setIdUnidade(entity.getUnidade() != null ? entity.getUnidade().getId() : null);
        dto.setIdTarefa(entity.getTarefa() != null ? entity.getTarefa().getId() : null);
        dto.setIdCiclo(entity.getCiclo() != null ? entity.getCiclo().getId() : null);
        dto.setStatusKanban(entity.getStatusKanban() != null ? entity.getStatusKanban().getDescricao() : null);
        dto.setDataConclusao(entity.getDataConclusao());
        return dto;
    }
}
