package school.sptech.APIDesbravadores.mapper;

import school.sptech.APIDesbravadores.domain.Evidencia;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;
import school.sptech.APIDesbravadores.dto.EvidenciaAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.EvidenciaCriacaoDto;
import school.sptech.APIDesbravadores.dto.EvidenciaResponseDto;

public class EvidenciaMapper {

    public static Evidencia toEntity(EvidenciaCriacaoDto dto, TarefaUnidade tarefaUnidade) {
        if (dto == null) return null;
        Evidencia entity = new Evidencia();
        entity.setUnidadeTarefa(tarefaUnidade);
        entity.setUrlS3(dto.getUrlS3());
        entity.setComentarioFeedback(dto.getComentarioFeedback());
        return entity;
    }

    public static void updateEntity(EvidenciaAtualizacaoDto dto, Evidencia entity, TarefaUnidade tarefaUnidade) {
        if (dto == null || entity == null) return;
        entity.setUnidadeTarefa(tarefaUnidade);
        entity.setUrlS3(dto.getUrlS3());
        entity.setComentarioFeedback(dto.getComentarioFeedback());
    }

    public static EvidenciaResponseDto toResponseDto(Evidencia entity) {
        if (entity == null) return null;
        EvidenciaResponseDto dto = new EvidenciaResponseDto();
        dto.setId(entity.getId());
        dto.setIdTarefaUnidade(entity.getUnidadeTarefa() != null ? entity.getUnidadeTarefa().getId() : null);
        dto.setUrlS3(entity.getUrlS3());
        dto.setComentarioFeedback(entity.getComentarioFeedback());
        dto.setDataEnvio(entity.getDataEnvio());
        return dto;
    }
}
