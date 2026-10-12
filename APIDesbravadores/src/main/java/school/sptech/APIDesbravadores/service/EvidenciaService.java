package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.Evidencia;
import school.sptech.APIDesbravadores.domain.StatusKanban;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;
import school.sptech.APIDesbravadores.dto.EvidenciaAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.EvidenciaCriacaoDto;
import school.sptech.APIDesbravadores.dto.EvidenciaResponseDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.exception.RequisicaoInvalidaException;
import school.sptech.APIDesbravadores.mapper.EvidenciaMapper;
import school.sptech.APIDesbravadores.repository.EvidenciaRepository;
import school.sptech.APIDesbravadores.repository.TarefaUnidadeRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EvidenciaService {

    private final EvidenciaRepository evidenciaRepository;
    private final TarefaUnidadeRepository tarefaUnidadeRepository;
    private final NotificacaoService notificacaoService;

    public EvidenciaService(EvidenciaRepository evidenciaRepository, TarefaUnidadeRepository tarefaUnidadeRepository,
                            NotificacaoService notificacaoService) {
        this.evidenciaRepository = evidenciaRepository;
        this.tarefaUnidadeRepository = tarefaUnidadeRepository;
        this.notificacaoService = notificacaoService;
    }

    @Transactional(readOnly = true)
    public List<EvidenciaResponseDto> findAll() {
        return evidenciaRepository.findAll().stream()
                .map(EvidenciaMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public EvidenciaResponseDto findById(Integer id) {
        Evidencia evidencia = evidenciaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Evidência não encontrada com ID: " + id));
        return EvidenciaMapper.toResponseDto(evidencia);
    }

    @Transactional
    public EvidenciaResponseDto create(EvidenciaCriacaoDto dto) {
        TarefaUnidade tarefaUnidade = tarefaUnidadeRepository.findById(dto.getIdTarefaUnidade())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("TarefaUnidade não encontrada com ID: " + dto.getIdTarefaUnidade()));
        Evidencia evidencia = EvidenciaMapper.toEntity(dto, tarefaUnidade);
        evidenciaRepository.save(evidencia);
        return EvidenciaMapper.toResponseDto(evidencia);
    }

    @Transactional
    public EvidenciaResponseDto update(Integer id, EvidenciaAtualizacaoDto dto) {
        Evidencia evidencia = evidenciaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Evidência não encontrada com ID: " + id));
        TarefaUnidade tarefaUnidade = null;
        if (dto.getIdTarefaUnidade() != null) {
            tarefaUnidade = tarefaUnidadeRepository.findById(dto.getIdTarefaUnidade())
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("TarefaUnidade não encontrada com ID: " + dto.getIdTarefaUnidade()));
        }
        EvidenciaMapper.updateEntity(dto, evidencia, tarefaUnidade);

        if (dto.getStatus() != null) {
            TarefaUnidade vinculo = evidencia.getUnidadeTarefa();
            if (vinculo == null) {
                throw new RequisicaoInvalidaException("Evidência sem tarefa vinculada");
            }
            aplicarDecisao(vinculo, dto.getStatus());
            if (vinculo.getStatusKanban() == StatusKanban.CONCLUIDA) {
                vinculo.setPontuacaoConcedida(dto.getPontuacao());
            } else {
                vinculo.setPontuacaoConcedida(null);
            }
            tarefaUnidadeRepository.save(vinculo);
            notificarDecisao(vinculo, evidencia);
        }
        evidenciaRepository.save(evidencia);
        return EvidenciaMapper.toResponseDto(evidencia);
    }

    /**
     * A pontuação da unidade (vw_resumo_unidades) soma Tarefa.pontuacao das
     * tarefas CONCLUIDA, então aprovar = mover o vínculo para CONCLUIDA.
     */
    private void aplicarDecisao(TarefaUnidade vinculo, String status) {
        switch (status.trim().toUpperCase()) {
            case "APROVADA", "APROVADO", "RECONHECIDO", "CONCLUIDA" -> {
                vinculo.setStatusKanban(StatusKanban.CONCLUIDA);
                vinculo.setDataConclusao(LocalDateTime.now());
            }
            case "CORRECAO_SOLICITADA" -> {
                vinculo.setStatusKanban(StatusKanban.EM_ANDAMENTO);
                vinculo.setDataConclusao(null);
            }
            case "PENDENTE", "EM_REVISAO" -> {
                vinculo.setStatusKanban(StatusKanban.EM_REVISAO);
                vinculo.setDataConclusao(null);
            }
            default -> throw new RequisicaoInvalidaException("Status de evidência inválido: " + status);
        }
    }

    private void notificarDecisao(TarefaUnidade vinculo, Evidencia evidencia) {
        String tarefa = vinculo.getTarefa() != null ? vinculo.getTarefa().getTitulo() : "tarefa";
        if (vinculo.getStatusKanban() == StatusKanban.CONCLUIDA) {
            Integer pts = vinculo.getPontuacaoConcedida() != null ? vinculo.getPontuacaoConcedida()
                    : (vinculo.getTarefa() != null ? vinculo.getTarefa().getPontuacao() : null);
            notificacaoService.notificarUnidade(vinculo.getUnidade(), "Tarefa aprovada",
                    "Sua evidência para \"" + tarefa + "\" foi aprovada!" + (pts != null ? " +" + pts + " pontos" : ""),
                    NotificacaoService.EVIDENCIA_APROVADA, evidencia.getId());
        } else if (vinculo.getStatusKanban() == StatusKanban.EM_ANDAMENTO) {
            notificacaoService.notificarUnidade(vinculo.getUnidade(), "Correção solicitada",
                    "A evidência de \"" + tarefa + "\" precisa de ajustes. Verifique os comentários.",
                    NotificacaoService.EVIDENCIA_CORRECAO, evidencia.getId());
        }
    }

    @Transactional(readOnly = true)
    public List<EvidenciaResponseDto> findAllByClube(Integer idClube) {
        return evidenciaRepository.findAll().stream()
                .filter(e -> e.getUnidadeTarefa() != null
                        && e.getUnidadeTarefa().getUnidade() != null
                        && e.getUnidadeTarefa().getUnidade().getClube() != null
                        && idClube.equals(e.getUnidadeTarefa().getUnidade().getClube().getId()))
                .map(EvidenciaMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public void delete(Integer id) {
        if (!evidenciaRepository.existsById(id)) {
            throw new EntidadeNaoEncontradaException("Evidência não encontrada com ID: " + id);
        }
        evidenciaRepository.deleteById(id);
    }
}
