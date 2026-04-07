package school.sptech.api_tasks.mapper;

import school.sptech.api_tasks.domain.StatusKanban;
import school.sptech.api_tasks.domain.Tarefa;
import school.sptech.api_tasks.domain.TarefaUnidade;
import school.sptech.api_tasks.dto.TarefaCreateDto;
import school.sptech.api_tasks.dto.TarefaResponseDto;
import school.sptech.api_tasks.dto.TarefaUpdateDto;

public class TarefaMapper {

    public static Tarefa toEntity(TarefaCreateDto dto) {
        if (dto == null) return null;
        Tarefa entity = new Tarefa();
        entity.setFkClube(dto.getFkClube());
        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        entity.setPontuacao(dto.getPontuacao());
        entity.setPrazoEntrega(dto.getPrazoEntrega());
        return entity;
    }

    public static void updateEntity(TarefaUpdateDto dto, Tarefa entity) {
        if (dto == null || entity == null) return;
        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        entity.setPontuacao(dto.getPontuacao());
        entity.setPrazoEntrega(dto.getPrazoEntrega());
    }

    public static TarefaResponseDto toResponseDto(Tarefa entity, TarefaUnidade tu) {
        if (entity == null) return null;
        TarefaResponseDto dto = new TarefaResponseDto();
        dto.setId(entity.getId());
        dto.setFkClube(entity.getFkClube());
        dto.setNome(entity.getNome());
        dto.setDescricao(entity.getDescricao());
        dto.setPontuacao(entity.getPontuacao());
        dto.setPrazoEntrega(entity.getPrazoEntrega());
        dto.setDataCriacao(entity.getDataCriacao());

        if (tu != null) {
            dto.setFkUnidade(tu.getFkUnidade());
            if (tu.getStatusKanban() != null) {
                dto.setStatusKanban(tu.getStatusKanban().getDescricao());
            }
        }
        return dto;
    }
}
