package school.sptech.APIDesbravadores.service;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.*;
import school.sptech.APIDesbravadores.dto.*;
import school.sptech.APIDesbravadores.exception.*;
import school.sptech.APIDesbravadores.mapper.TarefaMapper;
import school.sptech.APIDesbravadores.repository.*;

@Service
@Transactional
public class TarefaService {
    private final TarefaRepository tarefas;
    private final TarefaUnidadeRepository vinculos;
    private final UnidadeRepository unidades;
    private final ClubeRepository clubes;
    private final CicloRepository ciclos;
    private final CadernoRepository cadernos;
    private final EvidenciaRepository evidencias;
    private final AcessoService acesso;
    private final NotificacaoService notificacoes;

    public TarefaService(TarefaRepository tarefas, TarefaUnidadeRepository vinculos, UnidadeRepository unidades,
            ClubeRepository clubes, CicloRepository ciclos, CadernoRepository cadernos,
            EvidenciaRepository evidencias, AcessoService acesso, NotificacaoService notificacoes) {
        this.tarefas = tarefas; this.vinculos = vinculos; this.unidades = unidades; this.clubes = clubes;
        this.ciclos = ciclos; this.cadernos = cadernos; this.evidencias = evidencias;
        this.acesso = acesso; this.notificacoes = notificacoes;
    }

    public TarefaResponseDto create(TarefaCreateDto dto) {
        acesso.exigirDiretoria();
        Integer clubeId = acesso.atual().getIdClube();
        if (dto.getFkClube() != null) acesso.validarClube(dto.getFkClube());
        var clube = clubes.buscarBloqueado(clubeId).orElseThrow(() -> new EntidadeNaoEncontradaException("Clube não encontrado."));
        if (dto.getRequestId() != null) {
            var existente = tarefas.findByRequestId(dto.getRequestId());
            if (existente.isPresent()) {
                acesso.validarClube(existente.get().getClube().getId());
                return resumo(existente.get());
            }
        }
        var ciclo = ciclo(dto.getIdCiclo(), clubeId);
        var selecionadas = selecionarUnidades(dto.getUnidadeIds(), dto.getFkUnidade(), dto.getTodasUnidades(), clubeId);
        var tarefa = TarefaMapper.toEntity(dto);
        tarefa.setClube(clube);
        tarefa.setRequestId(dto.getRequestId());
        tarefa.setTipoTarefa(dto.getTipoTarefa());
        if (!Set.of("GERAL", "CADERNO").contains(dto.getTipoTarefa())) throw new RequisicaoInvalidaException("Tipo de tarefa inválido.");
        if ("CADERNO".equals(dto.getTipoTarefa())) {
            if (dto.getIdCaderno() == null) throw new RequisicaoInvalidaException("Selecione um caderno.");
            tarefa.setCaderno(cadernos.findById(dto.getIdCaderno()).orElseThrow(() -> new EntidadeNaoEncontradaException("Caderno não encontrado.")));
            acesso.validarClube(tarefa.getCaderno().getClube().getId());
        }
        tarefa.setInstrucoesEvidencia(dto.getInstrucoesEvidencia()); tarefa.setDataInicio(dto.getDataInicio());
        tarefas.save(tarefa);
        atribuir(tarefa, ciclo, selecionadas);
        return resumo(tarefa);
    }

    private Ciclo ciclo(Integer id, Integer clubeId) {
        if (id != null) {
            var ciclo = ciclos.findById(id).orElseThrow(() -> new EntidadeNaoEncontradaException("Ciclo não encontrado."));
            acesso.validarClube(ciclo.getClube().getId());
            if (!Boolean.TRUE.equals(ciclo.getAtivo())) throw new RequisicaoInvalidaException("O ciclo deve estar ativo.");
            return ciclo;
        }
        var ativos = ciclos.findByClubeIdAndAtivoTrue(clubeId);
        if (ativos.size() != 1) throw new RequisicaoInvalidaException("Cadastre um ciclo ativo ou informe idCiclo quando houver vários ciclos ativos.");
        return ativos.getFirst();
    }

    private List<Unidade> selecionarUnidades(List<Integer> ids, Integer legado, Boolean todas, Integer clubeId) {
        List<Unidade> resultado;
        if (Boolean.TRUE.equals(todas)) resultado = unidades.findByClubeId(clubeId);
        else {
            var escolhidas = ids != null ? ids : legado == null ? List.<Integer>of() : List.of(legado);
            resultado = new LinkedHashSet<>(escolhidas).stream().map(id -> unidades.findById(id)
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Unidade não encontrada: " + id))).toList();
        }
        if (resultado.isEmpty()) throw new RequisicaoInvalidaException("Selecione ao menos uma unidade.");
        resultado.forEach(u -> acesso.validarClube(u.getClube().getId()));
        return resultado;
    }

    private void atribuir(Tarefa tarefa, Ciclo ciclo, List<Unidade> selecionadas) {
        var existentes = vinculos.findAllByTarefaId(tarefa.getId());
        List<Unidade> novas = new ArrayList<>();
        for (var unidade : selecionadas) {
            if (existentes.stream().anyMatch(v -> v.getUnidade().getId().equals(unidade.getId()) && v.getCiclo().getId().equals(ciclo.getId()))) continue;
            var tu = new TarefaUnidade(); tu.setTarefa(tarefa); tu.setUnidade(unidade); tu.setCiclo(ciclo);
            tu.setStatusKanban(StatusKanban.A_FAZER); tu.setPrazoEntrega(tarefa.getPrazoEntrega());
            vinculos.save(tu); novas.add(unidade);
        }
        if (!novas.isEmpty()) notificacoes.novasAtribuicoes(tarefa, novas);
    }

    public TarefaResponseDto update(Integer id, TarefaUpdateDto dto) {
        acesso.exigirDiretoria();
        var tarefa = tarefas.buscarBloqueada(id).orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa não encontrada."));
        acesso.validarClube(tarefa.getClube().getId());
        TarefaMapper.updateEntity(dto, tarefa);
        tarefa.setInstrucoesEvidencia(dto.getInstrucoesEvidencia()); tarefa.setDataInicio(dto.getDataInicio());
        if (dto.getUnidadeIds() != null || Boolean.TRUE.equals(dto.getTodasUnidades())) {
            var atuais = vinculos.findAllByTarefaId(id);
            var ativos = atuais.stream().map(TarefaUnidade::getCiclo).filter(c -> Boolean.TRUE.equals(c.getAtivo()))
                    .collect(Collectors.toMap(Ciclo::getId, c -> c, (a, b) -> a));
            if (ativos.size() != 1) throw new RequisicaoInvalidaException("Selecione uma tarefa com exatamente um ciclo ativo.");
            atribuir(tarefa, ativos.values().iterator().next(),
                    selecionarUnidades(dto.getUnidadeIds(), null, dto.getTodasUnidades(), tarefa.getClube().getId()));
        }
        tarefas.save(tarefa);
        return resumo(tarefa);
    }

    @Transactional(readOnly = true)
    public List<TarefaResponseDto> findAll() {
        acesso.exigirDiretoria();
        return tarefas.findByClubeId(acesso.atual().getIdClube()).stream().map(this::resumo).toList();
    }

    public TarefaResponseDto findById(Integer id) {
        if (!acesso.diretoria()) return findStatusByTarefaId(id);
        var tarefa = tarefas.findById(id).orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa não encontrada."));
        acesso.validarClube(tarefa.getClube().getId()); return resumo(tarefa);
    }

    private TarefaResponseDto resumo(Tarefa tarefa) {
        var dto = TarefaMapper.toResponseDto(tarefa, null);
        var atribuicoes = vinculos.findAllByTarefaId(tarefa.getId());
        dto.setUnidadeIds(atribuicoes.stream().map(v -> v.getUnidade().getId()).distinct().toList());
        dto.setTotalUnidades(dto.getUnidadeIds().size());
        dto.setEntregas(atribuicoes.stream().filter(v -> v.getStatusKanban() == StatusKanban.CONCLUIDO).count());
        return dto;
    }

    public TarefaUnidade buscarDaUnidade(Integer id) {
        acesso.exigirConselheiro();
        var encontrados = vinculos.buscarIdsAtivos(id, acesso.atual().getIdUnidade());
        if (encontrados.size() != 1) throw new EntidadeNaoEncontradaException("Tarefa não encontrada ou vínculo ambíguo para a unidade no ciclo ativo.");
        var tu = vinculos.buscarBloqueado(encontrados.getFirst())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Vínculo não encontrado."));
        acesso.validarVinculo(tu); return tu;
    }

    public TarefaResponseDto findStatusByTarefaId(Integer id) {
        var tu = buscarDaUnidade(id); return TarefaMapper.toResponseDto(tu.getTarefa(), tu);
    }

    public TarefaResponseDto updateStatus(Integer id, String valor) {
        var tu = buscarDaUnidade(id);
        var novo = StatusKanban.fromString(valor);
        if (novo == null) throw new RequisicaoInvalidaException("Status inválido.");
        var anterior = tu.getStatusKanban();
        if (novo == anterior) return TarefaMapper.toResponseDto(tu.getTarefa(), tu);
        if (novo == StatusKanban.CONCLUIDO || anterior == StatusKanban.CONCLUIDO || anterior == StatusKanban.EM_REVISAO)
            throw new RequisicaoInvalidaException("Somente a Diretoria pode concluir ou devolver uma tarefa em revisão.");
        if (novo == StatusKanban.EM_REVISAO && !evidencias.existsByTarefaUnidadeId(tu.getId()))
            throw new RequisicaoInvalidaException("Anexe uma evidência antes de enviar para revisão.");
        tu.setStatusKanban(novo); vinculos.save(tu);
        if (novo == StatusKanban.EM_REVISAO) notificacoes.emRevisao(tu, acesso.atual().getNome());
        return TarefaMapper.toResponseDto(tu.getTarefa(), tu);
    }

    @Transactional(readOnly = true)
    public Map<String, List<TarefaResponseDto>> getKanban() {
        acesso.exigirConselheiro();
        return vinculos.findAllByUnidadeIdAndCicloAtivoTrue(acesso.atual().getIdUnidade()).stream()
                .filter(v -> v.getTarefa().getClube().getId().equals(acesso.atual().getIdClube()))
                .map(v -> {
                    var dto = TarefaMapper.toResponseDto(v.getTarefa(), v);
                    evidencias.findFirstByTarefaUnidadeIdOrderByIdDesc(v.getId())
                            .ifPresent(e -> dto.setComentarioFeedback(e.getComentarioFeedback()));
                    return dto;
                }).collect(Collectors.groupingBy(TarefaResponseDto::getStatusKanban));
    }

    public void delete(Integer id) {
        acesso.exigirDiretoria();
        var tarefa = tarefas.buscarBloqueada(id).orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa não encontrada."));
        acesso.validarClube(tarefa.getClube().getId());
        var atribuicoes = vinculos.findAllByTarefaId(id);
        if (atribuicoes.stream().anyMatch(v -> evidencias.existsByTarefaUnidadeId(v.getId())))
            throw new RequisicaoInvalidaException("Não é possível excluir uma tarefa com evidências.");
        vinculos.deleteAll(atribuicoes); tarefas.delete(tarefa);
    }
}
