package school.sptech.APIDesbravadores.mapper;

import school.sptech.APIDesbravadores.domain.Caderno;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.Tarefa;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;
import school.sptech.APIDesbravadores.dto.TarefaCreateDto;
import school.sptech.APIDesbravadores.dto.TarefaResponseDto;
import school.sptech.APIDesbravadores.dto.TarefaUpdateDto;

public class TarefaMapper {

    public static Tarefa toEntity(TarefaCreateDto dto, Clube clube, Caderno caderno) {
        if (dto == null) return null;
        Tarefa entity = new Tarefa();
        entity.setClube(clube);
        entity.setCaderno(caderno);
        entity.setTitulo(dto.getTitulo());
        entity.setDescricao(dto.getDescricao());
        entity.setTipoTarefa(dto.getTipoTarefa());
        entity.setPontuacao(dto.getPontuacao());
        entity.setPrazoPadrao(dto.getPrazoPadrao());
        return entity;
    }

    public static void updateEntity(TarefaUpdateDto dto, Tarefa entity) {
        if (dto == null || entity == null) return;
        entity.setTitulo(dto.getTitulo());
        entity.setDescricao(dto.getDescricao());
        entity.setPontuacao(dto.getPontuacao());
        entity.setPrazoPadrao(dto.getPrazoPadrao());
    }

    public static TarefaResponseDto toResponseDto(Tarefa entity, TarefaUnidade tu) {
        if (entity == null) return null;
        TarefaResponseDto dto = new TarefaResponseDto();
        dto.setId(entity.getId());
        dto.setFkClube(entity.getClube() != null ? entity.getClube().getId() : null);
        dto.setFkCaderno(entity.getCaderno() != null ? entity.getCaderno().getId() : null);
        dto.setTitulo(entity.getTitulo());
        dto.setDescricao(entity.getDescricao());
        dto.setTipoTarefa(entity.getTipoTarefa());
        dto.setPontuacao(entity.getPontuacao());
        dto.setPrazoPadrao(entity.getPrazoPadrao());

        if (tu != null) {
            dto.setFkUnidade(tu.getUnidade() != null ? tu.getUnidade().getId() : null);
            if (tu.getStatusKanban() != null) {
                dto.setStatusKanban(tu.getStatusKanban().getDescricao());
            }
        }
        return dto;
    }
}
