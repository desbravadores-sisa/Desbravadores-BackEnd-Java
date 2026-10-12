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
    private final ArmazenamentoArquivoService armazenamento;
    private final school.sptech.APIDesbravadores.repository.CicloRepository cicloRepository;

    public EvidenciaService(EvidenciaRepository evidenciaRepository, TarefaUnidadeRepository tarefaUnidadeRepository,
                            NotificacaoService notificacaoService) {
        this(evidenciaRepository, tarefaUnidadeRepository, notificacaoService, null, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public EvidenciaService(EvidenciaRepository evidenciaRepository, TarefaUnidadeRepository tarefaUnidadeRepository,
                            NotificacaoService notificacaoService, ArmazenamentoArquivoService armazenamento,
                            school.sptech.APIDesbravadores.repository.CicloRepository cicloRepository) {
        this.evidenciaRepository = evidenciaRepository;
        this.tarefaUnidadeRepository = tarefaUnidadeRepository;
        this.notificacaoService = notificacaoService;
        this.armazenamento = armazenamento;
        this.cicloRepository = cicloRepository;
    }

    /**
     * Conselheiro envia a evidência (foto/PDF) de uma tarefa de CLUBE da sua unidade.
     * O arquivo vai para a pasta de uploads e a tarefa passa para EM REVISAO (aguarda a Diretoria).
     */
    @Transactional
    public EvidenciaResponseDto enviar(Integer idTarefa, org.springframework.web.multipart.MultipartFile arquivo,
                                       String comentario, Integer idUnidade, Integer idClube) {
        if (idUnidade == null) {
            throw new EntidadeNaoEncontradaException("Conselheiro sem unidade vinculada");
        }
        Integer idCiclo = idClube == null ? null
                : cicloRepository.findByClubeIdAndAtivoTrue(idClube).map(c -> c.getId()).orElse(null);
        java.util.List<TarefaUnidade> candidatos = tarefaUnidadeRepository.findByTarefaIdAndUnidadeId(idTarefa, idUnidade);
        TarefaUnidade tu = candidatos.stream()
                .filter(v -> idCiclo != null && v.getCiclo() != null && idCiclo.equals(v.getCiclo().getId()))
                .findFirst()
                .or(() -> candidatos.stream().max(java.util.Comparator.comparing(TarefaUnidade::getId)))
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa " + idTarefa + " não está vinculada à sua unidade"));
        if (tu.getTarefa() != null && "CADERNO".equalsIgnoreCase(tu.getTarefa().getTipoTarefa())) {
            throw new RequisicaoInvalidaException("Tarefas de caderno usam o check-in das crianças, não envio de arquivo");
        }
        if (tu.getStatusKanban() == StatusKanban.CONCLUIDA) {
            throw new RequisicaoInvalidaException("Tarefa já concluída");
        }
        if (tu.getStatusKanban() == StatusKanban.EM_REVISAO) {
            throw new RequisicaoInvalidaException("Já existe evidência aguardando a Diretoria");
        }
        String url = armazenamento.salvar(arquivo, false);
        Evidencia evidencia = new Evidencia();
        evidencia.setUnidadeTarefa(tu);
        evidencia.setUrlS3(url);
        evidencia.setComentarioFeedback(comentario == null || comentario.isBlank() ? null : comentario.trim());
        evidencia.setDataEnvio(LocalDateTime.now());
        evidenciaRepository.save(evidencia);
        tu.setStatusKanban(StatusKanban.EM_REVISAO);
        tu.setDataConclusao(null);
        tarefaUnidadeRepository.save(tu);
        return EvidenciaMapper.toResponseDto(evidencia);
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
        Evidencia evidencia = evidenciaRepository.findById(id).orElseThrow();
        String url = evidencia.getUrlS3();
        TarefaUnidade vinculo = evidencia.getUnidadeTarefa();
        evidenciaRepository.deleteById(id);
        // Sem a evidência, a tarefa não pode ficar presa em "Em revisão" (o conselheiro não a tira de lá).
        if (vinculo != null && vinculo.getStatusKanban() == StatusKanban.EM_REVISAO
                && evidenciaRepository.findByUnidadeTarefaIdIn(List.of(vinculo.getId())).isEmpty()) {
            vinculo.setStatusKanban(StatusKanban.EM_ANDAMENTO);
            tarefaUnidadeRepository.save(vinculo);
        }
        if (armazenamento != null) {
            armazenamento.remover(url);
        }
    }
}
