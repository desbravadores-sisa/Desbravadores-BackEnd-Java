package school.sptech.APIDesbravadores.mapper;

import school.sptech.APIDesbravadores.domain.Evidencia;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;
import school.sptech.APIDesbravadores.dto.EvidenciaCreateDto;
import school.sptech.APIDesbravadores.dto.EvidenciaResponseDto;
import school.sptech.APIDesbravadores.dto.EvidenciaUpdateDto;

public class EvidenciaMapper {

    public static Evidencia toEntity(EvidenciaCreateDto dto, TarefaUnidade tarefaUnidade) {
        if (dto == null) return null;
        Evidencia evidencia = new Evidencia();
        evidencia.setTarefaUnidade(tarefaUnidade);
        evidencia.setNome(dto.getNome());
        evidencia.setUrlAnexo(dto.getUrlAnexo());
        return evidencia;
    }

    public static void updateEntity(EvidenciaUpdateDto dto, Evidencia evidencia) {
        if (dto == null || evidencia == null) return;
        evidencia.setNome(dto.getNome());
        evidencia.setUrlAnexo(dto.getUrlAnexo());
    }

    public static EvidenciaResponseDto toResponseDto(Evidencia evidencia) {
        if (evidencia == null) return null;
        EvidenciaResponseDto dto = new EvidenciaResponseDto();
        dto.setId(evidencia.getId());
        dto.setNome(evidencia.getNome());
        dto.setUrlAnexo(evidencia.getUrlAnexo());
        dto.setDataUpload(evidencia.getDataUpload());
        dto.setComentarioFeedback(evidencia.getComentarioFeedback());
        dto.setDataAnalise(evidencia.getDataAnalise());
        dto.setRevisorId(evidencia.getRevisor() == null ? null : evidencia.getRevisor().getId());

        TarefaUnidade tarefaUnidade = evidencia.getTarefaUnidade();
        if (tarefaUnidade != null) {
            dto.setIdTarefaUnidade(tarefaUnidade.getId());
            dto.setIdUnidade(tarefaUnidade.getUnidade().getId());
            dto.setNomeUnidade(tarefaUnidade.getUnidade().getNome());
            dto.setPontuacaoConcedida(tarefaUnidade.getPontuacaoConcedida());
            if (tarefaUnidade.getTarefa() != null) {
                dto.setIdTarefa(tarefaUnidade.getTarefa().getId());
                dto.setNomeTarefa(tarefaUnidade.getTarefa().getNome());
                dto.setPontuacao(tarefaUnidade.getTarefa().getPontuacao());
            }
            if (tarefaUnidade.getStatusKanban() != null) {
                dto.setStatusKanban(tarefaUnidade.getStatusKanban().getDescricao());
            }
        }

        return dto;
    }
}
