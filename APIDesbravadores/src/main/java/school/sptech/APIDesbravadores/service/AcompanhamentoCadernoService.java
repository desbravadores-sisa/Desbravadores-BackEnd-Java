package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.Caderno;
import school.sptech.APIDesbravadores.domain.ChecklistCaderno;
import school.sptech.APIDesbravadores.domain.Ciclo;
import school.sptech.APIDesbravadores.domain.Desbravador;
import school.sptech.APIDesbravadores.domain.DesbravadorCaderno;
import school.sptech.APIDesbravadores.domain.Evidencia;
import school.sptech.APIDesbravadores.domain.ExecucaoCaderno;
import school.sptech.APIDesbravadores.domain.StatusKanban;
import school.sptech.APIDesbravadores.domain.Tarefa;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;
import school.sptech.APIDesbravadores.dto.CadernoDoDesbravadorDto;
import school.sptech.APIDesbravadores.dto.CheckinDto;
import school.sptech.APIDesbravadores.dto.DesbravadorResponseDto;
import school.sptech.APIDesbravadores.dto.DesbravadorUnidadeDto;
import school.sptech.APIDesbravadores.dto.RequisitosDesbravadorDto;
import school.sptech.APIDesbravadores.dto.VinculoCadernoDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.exception.RequisicaoInvalidaException;
import school.sptech.APIDesbravadores.mapper.DesbravadorMapper;
import school.sptech.APIDesbravadores.repository.CadernoRepository;
import school.sptech.APIDesbravadores.repository.ChecklistCadernoRepository;
import school.sptech.APIDesbravadores.repository.CicloRepository;
import school.sptech.APIDesbravadores.repository.DesbravadorCadernoRepository;
import school.sptech.APIDesbravadores.repository.DesbravadorRepository;
import school.sptech.APIDesbravadores.repository.EvidenciaRepository;
import school.sptech.APIDesbravadores.repository.ExecucaoCadernoRepository;
import school.sptech.APIDesbravadores.repository.TarefaRepository;
import school.sptech.APIDesbravadores.repository.TarefaUnidadeRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Cadernos por desbravador:
 * <ul>
 *   <li>Diretoria: vincula/desvincula desbravadores a um caderno ("Ver Caderno");</li>
 *   <li>Conselheiro: check-in das crianças nas tarefas de caderno da sua unidade e
 *       acompanhamento dos requisitos individuais ("Desbravadores");</li>
 *   <li>conclusão do caderno por criança: quando ela cumpre todos os requisitos, o vínculo
 *       ganha data_conclusao (histórico exibido em "Membros").</li>
 * </ul>
 * Check-in: marcar todas as crianças vinculadas leva a tarefa para EM REVISAO e gera a
 * pendência de reconhecimento para a Diretoria (Evidência sem arquivo); desmarcar volta para
 * EM ANDAMENTO e retira a pendência.
 */
@Service
public class AcompanhamentoCadernoService {

    static final String TIPO_CADERNO = "CADERNO";
    /** Evidência automática do check-in: não tem arquivo; o front reconhece por este marcador. */
    static final String URL_CHECKIN = "checkin:caderno";

    private final CadernoRepository cadernoRepository;
    private final DesbravadorRepository desbravadorRepository;
    private final DesbravadorCadernoRepository vinculoRepository;
    private final TarefaRepository tarefaRepository;
    private final TarefaUnidadeRepository tarefaUnidadeRepository;
    private final ExecucaoCadernoRepository execucaoRepository;
    private final ChecklistCadernoRepository checklistRepository;
    private final CicloRepository cicloRepository;
    private final EvidenciaRepository evidenciaRepository;

    public AcompanhamentoCadernoService(CadernoRepository cadernoRepository, DesbravadorRepository desbravadorRepository,
                                        DesbravadorCadernoRepository vinculoRepository, TarefaRepository tarefaRepository,
                                        TarefaUnidadeRepository tarefaUnidadeRepository,
                                        ExecucaoCadernoRepository execucaoRepository,
                                        ChecklistCadernoRepository checklistRepository, CicloRepository cicloRepository,
                                        EvidenciaRepository evidenciaRepository) {
        this.cadernoRepository = cadernoRepository;
        this.desbravadorRepository = desbravadorRepository;
        this.vinculoRepository = vinculoRepository;
        this.tarefaRepository = tarefaRepository;
        this.tarefaUnidadeRepository = tarefaUnidadeRepository;
        this.execucaoRepository = execucaoRepository;
        this.checklistRepository = checklistRepository;
        this.cicloRepository = cicloRepository;
        this.evidenciaRepository = evidenciaRepository;
    }

    // =====================================================================
    // Diretoria — vínculo explícito ("Ver Caderno")
    // =====================================================================

    @Transactional(readOnly = true)
    public List<VinculoCadernoDto> listarVinculados(Integer idCaderno, Integer idClube) {
        Caderno caderno = cadernoDoClube(idCaderno, idClube);
        Set<Integer> requisitos = idsRequisitos(caderno.getId());
        return vinculoRepository.findByCadernoId(caderno.getId()).stream()
                .filter(v -> v.getDesbravador() != null && !Boolean.FALSE.equals(v.getDesbravador().getAtivo()))
                .sorted(Comparator.comparing(v -> v.getDesbravador().getNome(), String.CASE_INSENSITIVE_ORDER))
                .map(v -> {
                    Desbravador d = v.getDesbravador();
                    List<Integer> concluidos = requisitosConcluidos(d.getId(), caderno.getId()).stream()
                            .filter(requisitos::contains).sorted().toList();
                    return new VinculoCadernoDto(d.getId(), d.getNome(),
                            d.getUnidade() != null ? d.getUnidade().getId() : null,
                            d.getUnidade() != null ? d.getUnidade().getNome() : null,
                            d.getDataNascimento(), v.getDataVinculo(), v.getDataConclusao(), concluidos);
                })
                .toList();
    }

    /** Desbravadores ativos do clube ainda não vinculados ao caderno. */
    @Transactional(readOnly = true)
    public List<DesbravadorResponseDto> listarElegiveis(Integer idCaderno, Integer idClube) {
        Caderno caderno = cadernoDoClube(idCaderno, idClube);
        Set<Integer> jaVinculados = vinculoRepository.findByCadernoId(caderno.getId()).stream()
                .map(v -> v.getDesbravador().getId()).collect(Collectors.toSet());
        return desbravadorRepository.findByClubeId(idClube).stream()
                .filter(d -> !Boolean.FALSE.equals(d.getAtivo()) && !jaVinculados.contains(d.getId()))
                .sorted(Comparator.comparing(Desbravador::getNome, String.CASE_INSENSITIVE_ORDER))
                .map(DesbravadorMapper::toResponse)
                .toList();
    }

    @Transactional
    public VinculoCadernoDto vincular(Integer idCaderno, Integer idDesbravador, Integer idClube) {
        Caderno caderno = cadernoDoClube(idCaderno, idClube);
        Desbravador desbravador = desbravadorDoClube(idDesbravador, idClube);
        if (vinculoRepository.findByDesbravadorIdAndCadernoId(idDesbravador, idCaderno).isPresent()) {
            throw new RequisicaoInvalidaException("Desbravador já vinculado a este caderno");
        }
        DesbravadorCaderno vinculo = new DesbravadorCaderno();
        vinculo.setDesbravador(desbravador);
        vinculo.setCaderno(caderno);
        vinculo.setDataVinculo(LocalDateTime.now());
        vinculoRepository.save(vinculo);
        atualizarConclusao(desbravador.getId(), caderno.getId());
        return listarVinculados(idCaderno, idClube).stream()
                .filter(v -> v.idDesbravador().equals(idDesbravador)).findFirst().orElseThrow();
    }

    @Transactional
    public void desvincular(Integer idCaderno, Integer idDesbravador, Integer idClube) {
        cadernoDoClube(idCaderno, idClube);
        desbravadorDoClube(idDesbravador, idClube);
        DesbravadorCaderno vinculo = vinculoRepository.findByDesbravadorIdAndCadernoId(idDesbravador, idCaderno)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Desbravador não está vinculado a este caderno"));
        vinculoRepository.delete(vinculo);
    }

    /** Cadernos de cada desbravador (para "Membros"): atual e concluídos. */
    @Transactional(readOnly = true)
    public Map<Integer, List<CadernoDoDesbravadorDto>> cadernosPorDesbravador(List<Integer> idsDesbravador) {
        if (idsDesbravador.isEmpty()) {
            return Map.of();
        }
        return vinculoRepository.findByDesbravadorIdIn(idsDesbravador).stream()
                .collect(Collectors.groupingBy(v -> v.getDesbravador().getId(),
                        Collectors.mapping(v -> resumo(v, null), Collectors.toList())));
    }

    // =====================================================================
    // Conselheiro — check-in nas tarefas de caderno
    // =====================================================================

    @Transactional(readOnly = true)
    public CheckinDto obterCheckin(Integer idTarefa, Integer idUnidade, Integer idClube) {
        TarefaUnidade tu = vinculoTarefaDaUnidade(idTarefa, idUnidade, idClube);
        Tarefa tarefa = exigirTarefaDeCaderno(tu);
        return montarCheckin(tu, tarefa, idUnidade, idClube);
    }

    @Transactional
    public CheckinDto marcarCheckin(Integer idTarefa, Integer idDesbravador, boolean concluiu,
                                    Integer idUnidade, Integer idClube) {
        TarefaUnidade tu = vinculoTarefaDaUnidade(idTarefa, idUnidade, idClube);
        Tarefa tarefa = exigirTarefaDeCaderno(tu);
        if (tu.getStatusKanban() == StatusKanban.CONCLUIDA) {
            throw new RequisicaoInvalidaException("Tarefa já concluída pela Diretoria; o check-in não pode mudar");
        }
        List<Desbravador> elegiveis = elegiveisNaUnidade(tarefa.getCaderno().getId(), idUnidade);
        Desbravador desbravador = elegiveis.stream().filter(d -> d.getId().equals(idDesbravador)).findFirst()
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        "Desbravador não está na sua unidade ou não está vinculado ao caderno desta tarefa"));

        ExecucaoCaderno execucao = execucaoDe(tu, true);
        ChecklistCaderno item = checklistRepository
                .findFirstByExecucaoCadernoIdAndDesbravadorId(execucao.getId(), desbravador.getId())
                .orElseGet(() -> {
                    ChecklistCaderno novo = new ChecklistCaderno();
                    novo.setExecucaoCaderno(execucao);
                    novo.setDesbravador(desbravador);
                    return novo;
                });
        item.setConcluiuTarefa(concluiu);
        item.setDataMarcacao(LocalDateTime.now());
        checklistRepository.save(item);

        Set<Integer> marcados = marcadosNaExecucao(execucao.getId());
        long totalMarcados = elegiveis.stream().filter(d -> marcados.contains(d.getId())).count();
        StatusKanban novo = totalMarcados == elegiveis.size() ? StatusKanban.EM_REVISAO
                : (totalMarcados > 0 || tu.getStatusKanban() == StatusKanban.EM_REVISAO) ? StatusKanban.EM_ANDAMENTO
                : tu.getStatusKanban() == null ? StatusKanban.A_FAZER : tu.getStatusKanban();

        if (novo == StatusKanban.EM_REVISAO && tu.getStatusKanban() != StatusKanban.EM_REVISAO) {
            Evidencia pendencia = new Evidencia();
            pendencia.setUnidadeTarefa(tu);
            pendencia.setUrlS3(URL_CHECKIN);
            pendencia.setComentarioFeedback("Check-in concluído: " + elegiveis.size() + " de " + elegiveis.size()
                    + " desbravadores");
            pendencia.setDataEnvio(LocalDateTime.now());
            evidenciaRepository.save(pendencia);
        } else if (novo != StatusKanban.EM_REVISAO && tu.getStatusKanban() == StatusKanban.EM_REVISAO) {
            evidenciaRepository.findByUnidadeTarefaIdIn(List.of(tu.getId())).stream()
                    .filter(e -> URL_CHECKIN.equals(e.getUrlS3()))
                    .forEach(evidenciaRepository::delete);
        }
        tu.setStatusKanban(novo);
        tarefaUnidadeRepository.save(tu);
        execucao.setStatusKanban(novo);
        execucaoRepository.save(execucao);

        atualizarConclusao(desbravador.getId(), tarefa.getCaderno().getId());
        return montarCheckin(tu, tarefa, idUnidade, idClube);
    }

    // =====================================================================
    // Conselheiro — "Desbravadores" (requisitos individuais)
    // =====================================================================

    @Transactional(readOnly = true)
    public List<DesbravadorUnidadeDto> desbravadoresDaUnidade(Integer idUnidade) {
        exigirUnidade(idUnidade);
        List<Desbravador> criancas = desbravadorRepository.findByUnidadeId(idUnidade).stream()
                .filter(d -> !Boolean.FALSE.equals(d.getAtivo()))
                .sorted(Comparator.comparing(Desbravador::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList();
        Map<Integer, List<DesbravadorCaderno>> vinculos = criancas.isEmpty() ? Map.of()
                : vinculoRepository.findByDesbravadorIdIn(criancas.stream().map(Desbravador::getId).toList()).stream()
                .collect(Collectors.groupingBy(v -> v.getDesbravador().getId()));
        return criancas.stream()
                .map(d -> new DesbravadorUnidadeDto(d.getId(), d.getNome(), d.getDataNascimento(),
                        vinculos.getOrDefault(d.getId(), List.of()).stream()
                                .map(v -> resumo(v, requisitosConcluidos(d.getId(), v.getCaderno().getId())))
                                .sorted(Comparator.comparing(CadernoDoDesbravadorDto::idadeAlvo,
                                        Comparator.nullsLast(Comparator.naturalOrder())))
                                .toList()))
                .toList();
    }

    @Transactional(readOnly = true)
    public RequisitosDesbravadorDto requisitosDoDesbravador(Integer idDesbravador, Integer idUnidade, Integer idClube) {
        exigirUnidade(idUnidade);
        Desbravador d = desbravadorRepository.findById(idDesbravador)
                .filter(x -> x.getUnidade() != null && idUnidade.equals(x.getUnidade().getId()))
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Desbravador não encontrado na sua unidade"));
        Integer idCiclo = cicloAtivo(idClube).map(Ciclo::getId).orElse(null);
        Map<Integer, TarefaUnidade> noQuadro = tarefaUnidadeRepository.findByUnidadeId(idUnidade).stream()
                .filter(tu -> tu.getTarefa() != null)
                .filter(tu -> idCiclo == null || (tu.getCiclo() != null && idCiclo.equals(tu.getCiclo().getId())))
                .collect(Collectors.toMap(tu -> tu.getTarefa().getId(), Function.identity(), (a, b) -> a));

        List<RequisitosDesbravadorDto.Caderno> cadernos = vinculoRepository.findByDesbravadorId(d.getId()).stream()
                .sorted(Comparator.comparing(v -> v.getCaderno().getIdadeAlvo(), Comparator.nullsLast(Comparator.naturalOrder())))
                .map(v -> {
                    Caderno c = v.getCaderno();
                    Set<Integer> feitos = requisitosConcluidos(d.getId(), c.getId());
                    List<RequisitosDesbravadorDto.Requisito> reqs = requisitos(c.getId()).stream()
                            .map(t -> {
                                TarefaUnidade tu = noQuadro.get(t.getId());
                                boolean disponivel = tu != null && tu.getStatusKanban() != StatusKanban.CONCLUIDA;
                                return new RequisitosDesbravadorDto.Requisito(t.getId(), t.getTitulo(), t.getDescricao(),
                                        feitos.contains(t.getId()), disponivel);
                            })
                            .toList();
                    return new RequisitosDesbravadorDto.Caderno(c.getId(), c.getNome(), v.getDataConclusao(), reqs);
                })
                .toList();
        return new RequisitosDesbravadorDto(d.getId(), d.getNome(), cadernos);
    }

    // =====================================================================
    // Regras compartilhadas
    // =====================================================================

    /** Ids dos cadernos com pelo menos uma criança ativa da unidade vinculada (e ainda não concluído). */
    @Transactional(readOnly = true)
    public Set<Integer> cadernosEmAndamentoNaUnidade(Integer idUnidade) {
        return vinculoRepository.findByDesbravadorUnidadeId(idUnidade).stream()
                .filter(v -> v.getDataConclusao() == null && !Boolean.FALSE.equals(v.getDesbravador().getAtivo()))
                .map(v -> v.getCaderno().getId())
                .collect(Collectors.toSet());
    }

    /** Marca/desmarca a conclusão do caderno para a criança conforme os requisitos cumpridos. */
    @Transactional
    public void atualizarConclusao(Integer idDesbravador, Integer idCaderno) {
        vinculoRepository.findByDesbravadorIdAndCadernoId(idDesbravador, idCaderno).ifPresent(v -> {
            Set<Integer> reqs = idsRequisitos(idCaderno);
            boolean completo = !reqs.isEmpty() && requisitosConcluidos(idDesbravador, idCaderno).containsAll(reqs);
            if (completo && v.getDataConclusao() == null) {
                v.setDataConclusao(LocalDateTime.now());
                vinculoRepository.save(v);
            } else if (!completo && v.getDataConclusao() != null) {
                v.setDataConclusao(null);
                vinculoRepository.save(v);
            }
        });
    }

    private CadernoDoDesbravadorDto resumo(DesbravadorCaderno v, Set<Integer> concluidos) {
        Caderno c = v.getCaderno();
        Set<Integer> reqs = idsRequisitos(c.getId());
        Set<Integer> feitos = concluidos != null ? concluidos : requisitosConcluidos(v.getDesbravador().getId(), c.getId());
        int qtd = (int) feitos.stream().filter(reqs::contains).count();
        return new CadernoDoDesbravadorDto(c.getId(), c.getNome(), c.getIdadeAlvo(), v.getDataConclusao(), reqs.size(), qtd);
    }

    private CheckinDto montarCheckin(TarefaUnidade tu, Tarefa tarefa, Integer idUnidade, Integer idClube) {
        List<Desbravador> elegiveis = elegiveisNaUnidade(tarefa.getCaderno().getId(), idUnidade);
        Set<Integer> marcados = execucaoDe(tu, false) == null ? Set.of() : marcadosNaExecucao(execucaoDe(tu, false).getId());
        List<CheckinDto.Item> itens = elegiveis.stream()
                .map(d -> new CheckinDto.Item(d.getId(), d.getNome(), marcados.contains(d.getId())))
                .toList();
        int qtd = (int) itens.stream().filter(CheckinDto.Item::concluiu).count();
        String status = (tu.getStatusKanban() != null ? tu.getStatusKanban() : StatusKanban.A_FAZER).getDescricao();
        return new CheckinDto(tarefa.getId(), tarefa.getTitulo(), tarefa.getCaderno().getId(),
                tarefa.getCaderno().getNome(), status, itens.size(), qtd, itens);
    }

    private List<Desbravador> elegiveisNaUnidade(Integer idCaderno, Integer idUnidade) {
        return vinculoRepository.findByCadernoId(idCaderno).stream()
                .map(DesbravadorCaderno::getDesbravador)
                .filter(d -> d != null && !Boolean.FALSE.equals(d.getAtivo())
                        && d.getUnidade() != null && idUnidade.equals(d.getUnidade().getId()))
                .sorted(Comparator.comparing(Desbravador::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private ExecucaoCaderno execucaoDe(TarefaUnidade tu, boolean criar) {
        Optional<ExecucaoCaderno> existente = execucaoRepository.findFirstByUnidadeIdAndTarefaIdAndCicloIdOrderByIdAsc(
                tu.getUnidade().getId(), tu.getTarefa().getId(), tu.getCiclo().getId());
        if (existente.isPresent() || !criar) {
            return existente.orElse(null);
        }
        ExecucaoCaderno nova = new ExecucaoCaderno();
        nova.setUnidade(tu.getUnidade());
        nova.setTarefa(tu.getTarefa());
        nova.setCiclo(tu.getCiclo());
        nova.setStatusKanban(StatusKanban.A_FAZER);
        return execucaoRepository.save(nova);
    }

    private Set<Integer> marcadosNaExecucao(Integer idExecucao) {
        return checklistRepository.findByExecucaoCadernoId(idExecucao).stream()
                .filter(c -> Boolean.TRUE.equals(c.getConcluiuTarefa()) && c.getDesbravador() != null)
                .map(c -> c.getDesbravador().getId())
                .collect(Collectors.toSet());
    }

    /** Ids das tarefas do caderno que a criança já cumpriu (em qualquer ciclo). */
    private Set<Integer> requisitosConcluidos(Integer idDesbravador, Integer idCaderno) {
        return checklistRepository
                .findByDesbravadorIdAndConcluiuTarefaTrueAndExecucaoCadernoTarefaCadernoId(idDesbravador, idCaderno)
                .stream()
                .map(c -> c.getExecucaoCaderno().getTarefa().getId())
                .collect(Collectors.toSet());
    }

    private List<Tarefa> requisitos(Integer idCaderno) {
        return tarefaRepository.findByCadernoIdAndTipoTarefa(idCaderno, TIPO_CADERNO).stream()
                .sorted(Comparator.comparing(Tarefa::getId))
                .toList();
    }

    private Set<Integer> idsRequisitos(Integer idCaderno) {
        return requisitos(idCaderno).stream().map(Tarefa::getId).collect(Collectors.toSet());
    }

    private TarefaUnidade vinculoTarefaDaUnidade(Integer idTarefa, Integer idUnidade, Integer idClube) {
        exigirUnidade(idUnidade);
        Integer idCiclo = cicloAtivo(idClube).map(Ciclo::getId).orElse(null);
        List<TarefaUnidade> candidatos = tarefaUnidadeRepository.findByTarefaIdAndUnidadeId(idTarefa, idUnidade);
        return candidatos.stream()
                .filter(tu -> idCiclo != null && tu.getCiclo() != null && idCiclo.equals(tu.getCiclo().getId()))
                .findFirst()
                .or(() -> candidatos.stream().max(Comparator.comparing(TarefaUnidade::getId)))
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa " + idTarefa + " não está vinculada à sua unidade"));
    }

    private static Tarefa exigirTarefaDeCaderno(TarefaUnidade tu) {
        Tarefa tarefa = tu.getTarefa();
        if (!TIPO_CADERNO.equalsIgnoreCase(tarefa.getTipoTarefa()) || tarefa.getCaderno() == null) {
            throw new RequisicaoInvalidaException("Check-in só existe para tarefas de caderno; tarefas de clube usam envio de evidência");
        }
        return tarefa;
    }

    private Optional<Ciclo> cicloAtivo(Integer idClube) {
        return idClube == null ? Optional.empty() : cicloRepository.findByClubeIdAndAtivoTrue(idClube);
    }

    private Caderno cadernoDoClube(Integer idCaderno, Integer idClube) {
        return cadernoRepository.findById(idCaderno)
                .filter(c -> c.getClube() != null && Objects.equals(c.getClube().getId(), idClube))
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Caderno não encontrado no seu clube"));
    }

    private Desbravador desbravadorDoClube(Integer idDesbravador, Integer idClube) {
        return desbravadorRepository.findById(idDesbravador)
                .filter(d -> d.getClube() != null && Objects.equals(d.getClube().getId(), idClube))
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Desbravador não encontrado no seu clube"));
    }

    private static void exigirUnidade(Integer idUnidade) {
        if (idUnidade == null) {
            throw new EntidadeNaoEncontradaException("Conselheiro sem unidade vinculada");
        }
    }
}
