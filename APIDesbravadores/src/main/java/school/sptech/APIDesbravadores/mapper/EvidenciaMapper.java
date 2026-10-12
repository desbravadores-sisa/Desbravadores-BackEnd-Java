package school.sptech.APIDesbravadores.mapper;



import school.sptech.APIDesbravadores.domain.Evidencia;

import school.sptech.APIDesbravadores.domain.StatusKanban;

import school.sptech.APIDesbravadores.domain.Tarefa;

import school.sptech.APIDesbravadores.domain.TarefaUnidade;

import school.sptech.APIDesbravadores.domain.Unidade;

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



    /** Atualização parcial: só sobrescreve o que veio preenchido. */

    public static void updateEntity(EvidenciaAtualizacaoDto dto, Evidencia entity, TarefaUnidade tarefaUnidade) {

        if (dto == null || entity == null) return;

        if (tarefaUnidade != null) entity.setUnidadeTarefa(tarefaUnidade);

        if (dto.getUrlS3() != null) entity.setUrlS3(dto.getUrlS3());

        if (dto.getComentarioFeedback() != null) entity.setComentarioFeedback(dto.getComentarioFeedback());

    }



    public static EvidenciaResponseDto toResponseDto(Evidencia entity) {

        if (entity == null) return null;

        EvidenciaResponseDto dto = new EvidenciaResponseDto();

        dto.setId(entity.getId());

        dto.setUrlS3(entity.getUrlS3());

        dto.setComentarioFeedback(entity.getComentarioFeedback());

        dto.setDataEnvio(entity.getDataEnvio());



        TarefaUnidade tu = entity.getUnidadeTarefa();

        if (tu != null) {

            dto.setIdTarefaUnidade(tu.getId());

            StatusKanban sk = tu.getStatusKanban();

            dto.setStatusKanban(sk != null ? sk.getDescricao() : null);



            Tarefa t = tu.getTarefa();

            if (t != null) {

                dto.setIdTarefa(t.getId());

                dto.setTitulo(t.getTitulo());

                dto.setDescricao(t.getDescricao());

                dto.setPontuacaoPrevista(t.getPontuacao());
                dto.setPontuacao(tu.getPontuacaoConcedida() != null ? tu.getPontuacaoConcedida() : t.getPontuacao());

                dto.setTipoEvidencia(t.getTipoTarefa());

                dto.setFkCaderno(t.getCaderno() != null ? t.getCaderno().getId() : null);

            }

            Unidade u = tu.getUnidade();

            if (u != null) {

                dto.setIdUnidade(u.getId());

                dto.setNomeUnidade(u.getNome());

            }

            dto.setStatus(statusDecisao(sk, t, entity.getComentarioFeedback()));

        }

        return dto;

    }



    /** Traduz o status do Kanban para a decisão exibida na tela de Evidências. */

    static String statusDecisao(StatusKanban sk, Tarefa t, String feedback) {

        if (sk == null) return "PENDENTE";

        return switch (sk) {

            case CONCLUIDA -> (t != null && "CADERNO".equalsIgnoreCase(t.getTipoTarefa())) ? "RECONHECIDO" : "APROVADA";

            case EM_ANDAMENTO -> (feedback != null && !feedback.isBlank()) ? "CORRECAO_SOLICITADA" : "PENDENTE";

            default -> "PENDENTE";

        };

    }

}

