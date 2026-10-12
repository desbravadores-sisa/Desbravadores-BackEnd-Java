package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.Caderno;
import school.sptech.APIDesbravadores.domain.Ciclo;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.StatusKanban;
import school.sptech.APIDesbravadores.domain.Tarefa;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.dto.TarefaCreateDto;
import school.sptech.APIDesbravadores.dto.TarefaResponseDto;
import school.sptech.APIDesbravadores.dto.TarefaUpdateDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.exception.RequisicaoInvalidaException;
import school.sptech.APIDesbravadores.mapper.TarefaMapper;
import school.sptech.APIDesbravadores.repository.CadernoRepository;
import school.sptech.APIDesbravadores.repository.CicloRepository;
import school.sptech.APIDesbravadores.repository.DesbravadorRepository;
import school.sptech.APIDesbravadores.repository.EvidenciaRepository;
import school.sptech.APIDesbravadores.repository.ClubeRepository;
import school.sptech.APIDesbravadores.repository.TarefaRepository;
import school.sptech.APIDesbravadores.repository.TarefaUnidadeRepository;
import school.sptech.APIDesbravadores.repository.UnidadeRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final TarefaUnidadeRepository tarefaUnidadeRepository;
    private final ClubeRepository clubeRepository;
    private final UnidadeRepository unidadeRepository;
    private final CicloRepository cicloRepository;
    private final CadernoRepository cadernoRepository;
    private final DesbravadorRepository desbravadorRepository;
    private final EvidenciaRepository evidenciaRepository;

    public TarefaService(TarefaRepository tarefaRepository, TarefaUnidadeRepository tarefaUnidadeRepository,
                          ClubeRepository clubeRepository, UnidadeRepository unidadeRepository,
                          CicloRepository cicloRepository, CadernoRepository cadernoRepository) {
        this(tarefaRepository, tarefaUnidadeRepository, clubeRepository, unidadeRepository, cicloRepository,
                cadernoRepository, null, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public TarefaService(TarefaRepository tarefaRepository, TarefaUnidadeRepository tarefaUnidadeRepository,
                          ClubeRepository clubeRepository, UnidadeRepository unidadeRepository,
                          CicloRepository cicloRepository, CadernoRepository cadernoRepository,
                          DesbravadorRepository desbravadorRepository, EvidenciaRepository evidenciaRepository) {
        this.desbravadorRepository = desbravadorRepository;
        this.evidenciaRepository = evidenciaRepository;
        this.tarefaRepository = tarefaRepository;
        this.tarefaUnidadeRepository = tarefaUnidadeRepository;
        this.clubeRepository = clubeRepository;
        this.unidadeRepository = unidadeRepository;
        this.cicloRepository = cicloRepository;
        this.cadernoRepository = cadernoRepository;
    }

    @Transactional
    public TarefaResponseDto create(TarefaCreateDto dto) {
        if (dto.getFkClube() == null) {
            throw new RequisicaoInvalidaException("fkClube é obrigatório");
        }
        Clube clube = clubeRepository.findById(dto.getFkClube())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Clube não encontrado com ID: " + dto.getFkClube()));
        Caderno caderno = null;
        if (dto.getFkCaderno() != null) {
            caderno = cadernoRepository.findById(dto.getFkCaderno())
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Caderno não encontrado com ID: " + dto.getFkCaderno()));
        }

        // Com fkUnidade a tarefa vai para uma unidade; sem ela (fluxo da tela da
        // diretoria), é distribuída para todas as unidades do clube.
        List<Unidade> unidades;
        if (dto.getFkUnidade() != null) {
            unidades = List.of(unidadeRepository.findById(dto.getFkUnidade())
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Unidade não encontrada com ID: " + dto.getFkUnidade())));
        } else {
            unidades = unidadeRepository.findByClubeId(dto.getFkClube());
        }

        Ciclo ciclo = cicloRepository.findByClubeIdAndAtivoTrue(dto.getFkClube())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Nenhum ciclo ativo encontrado para o Clube ID: " + dto.getFkClube()));

        Tarefa tarefa = TarefaMapper.toEntity(dto, clube, caderno);
        Tarefa savedTarefa = tarefaRepository.save(tarefa);

        TarefaUnidade primeiro = null;
        for (Unidade unidade : unidades) {
            TarefaUnidade tu = new TarefaUnidade();
            tu.setTarefa(savedTarefa);
            tu.setUnidade(unidade);
            tu.setCiclo(ciclo);
            tu.setStatusKanban(StatusKanban.A_FAZER);
            tarefaUnidadeRepository.save(tu);
            if (primeiro == null) primeiro = tu;
        }
        return TarefaMapper.toResponseDto(savedTarefa, primeiro);
    }

    @Transactional(readOnly = true)
    public List<TarefaResponseDto> findAllByClube(Integer idClube) {
        return mapearComVinculos(tarefaRepository.findByClubeId(idClube));
    }

    @Transactional(readOnly = true)
    public List<TarefaResponseDto> findAll() {
        return mapearComVinculos(tarefaRepository.findAll());
    }

    private List<TarefaResponseDto> mapearComVinculos(List<Tarefa> tarefas) {
        if (tarefas.isEmpty()) {
            return List.of();
        }

        // Busca os vínculos de uma vez só. Consultar dentro do laço custava
        // uma query por tarefa (N+1).
        List<Integer> ids = tarefas.stream().map(Tarefa::getId).toList();
        Map<Integer, TarefaUnidade> vinculosPorTarefa = tarefaUnidadeRepository.findByTarefaIdIn(ids).stream()
                .collect(Collectors.toMap(
                        tu -> tu.getTarefa().getId(),
                        Function.identity(),
                        (existente, duplicado) -> existente));

        return tarefas.stream()
                .map(t -> TarefaMapper.toResponseDto(t, vinculosPorTarefa.get(t.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TarefaResponseDto findById(Integer id) {
        Tarefa t = tarefaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa não encontrada com ID: " + id));
        TarefaUnidade tu = tarefaUnidadeRepository.findFirstByTarefaIdOrderByIdAsc(t.getId()).orElse(null);
        return TarefaMapper.toResponseDto(t, tu);
    }

    @Transactional
    public TarefaResponseDto update(Integer id, TarefaUpdateDto dto) {
        Tarefa t = tarefaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa não encontrada com ID: " + id));
        TarefaMapper.updateEntity(dto, t);
        Tarefa saved = tarefaRepository.save(t);
        TarefaUnidade tu = tarefaUnidadeRepository.findFirstByTarefaIdOrderByIdAsc(saved.getId()).orElse(null);
        return TarefaMapper.toResponseDto(saved, tu);
    }

    @Transactional
    public void delete(Integer id) {
        Tarefa t = tarefaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa não encontrada com ID: " + id));

        tarefaUnidadeRepository.deleteByTarefaId(t.getId());
        tarefaRepository.delete(t);
    }

    public TarefaResponseDto findStatusByTarefaId(Integer id) {
        Tarefa t = tarefaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa não encontrada com ID: " + id));

        TarefaUnidade tu = tarefaUnidadeRepository.findFirstByTarefaIdOrderByIdAsc(t.getId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("TarefaUnidade não encontrada para Tarefa ID: " + id));

        return TarefaMapper.toResponseDto(t, tu);
    }

    // =====================================================================
    // Kanban do Conselheiro: sempre restrito à unidade do usuário logado
    // =====================================================================

    /**
     * Quadro da unidade do conselheiro no ciclo ativo do clube, agrupado pela descrição
     * do status ("A FAZER", "EM ANDAMENTO", "EM REVISAO", "CONCLUIDA"). Tarefas de caderno
     * só entram se a unidade tiver desbravador com a idade-alvo do caderno.
     */
    @Transactional(readOnly = true)
    public Map<String, List<TarefaResponseDto>> getKanbanDaUnidade(Integer idUnidade, Integer idClube) {
        if (idUnidade == null) {
            throw new EntidadeNaoEncontradaException("Conselheiro sem unidade vinculada");
        }
        Integer idCicloAtivo = idClube == null ? null
                : cicloRepository.findByClubeIdAndAtivoTrue(idClube).map(c -> c.getId()).orElse(null);
        java.time.LocalDate hoje = java.time.LocalDate.now();
        java.util.Set<Integer> idades = desbravadorRepository == null ? java.util.Set.of()
                : RegraVisibilidadeCaderno.idadesDaUnidade(desbravadorRepository.findByUnidadeId(idUnidade), hoje);
        Map<Integer, school.sptech.APIDesbravadores.domain.Caderno> cadernos = idClube == null ? Map.of()
                : cadernoRepository.findByClubeId(idClube).stream()
                .collect(Collectors.toMap(c -> c.getId(), Function.identity(), (a, b) -> a));

        List<TarefaUnidade> vinculos = tarefaUnidadeRepository.findByUnidadeId(idUnidade).stream()
                .filter(tu -> tu.getTarefa() != null)
                .filter(tu -> idCicloAtivo == null || (tu.getCiclo() != null && idCicloAtivo.equals(tu.getCiclo().getId())))
                // Concluídas sempre aparecem (são pontos já conquistados); a regra de idade vale para as abertas.
                .filter(tu -> tu.getStatusKanban() == StatusKanban.CONCLUIDA
                        || RegraVisibilidadeCaderno.visivel(tu.getTarefa(), cadernos, idades))
                .sorted(java.util.Comparator.comparing(
                        (TarefaUnidade tu) -> tu.getPrazoEntrega() != null ? tu.getPrazoEntrega().toLocalDate() : tu.getTarefa().getPrazoPadrao(),
                        java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())).thenComparing(TarefaUnidade::getId))
                .toList();

        java.util.Set<Integer> comEvidencia = vinculos.isEmpty() || evidenciaRepository == null ? java.util.Set.of()
                : evidenciaRepository.findByUnidadeTarefaIdIn(vinculos.stream().map(TarefaUnidade::getId).toList()).stream()
                .map(e -> e.getUnidadeTarefa().getId())
                .collect(Collectors.toSet());

        Map<String, List<TarefaResponseDto>> kanban = new LinkedHashMap<>();
        for (StatusKanban status : StatusKanban.values()) {
            kanban.put(status.getDescricao(), new ArrayList<>());
        }
        for (TarefaUnidade tu : vinculos) {
            StatusKanban status = tu.getStatusKanban() != null ? tu.getStatusKanban() : StatusKanban.A_FAZER;
            TarefaResponseDto dto = TarefaMapper.toResponseDto(tu.getTarefa(), tu);
            dto.setStatusKanban(status.getDescricao());
            dto.setPossuiEvidencia(comEvidencia.contains(tu.getId()));
            kanban.get(status.getDescricao()).add(dto);
        }
        return kanban;
    }

    /**
     * Move a tarefa no Kanban da unidade do conselheiro. O conselheiro não conclui tarefas:
     * "CONCLUIDA" só acontece quando a Diretoria aprova a evidência.
     */
    @Transactional
    public TarefaResponseDto updateStatusDaUnidade(Integer idTarefa, Integer idUnidade, String statusStr) {
        StatusKanban status = StatusKanban.fromString(statusStr);
        if (status == null) {
            throw new RequisicaoInvalidaException("Status inválido: " + statusStr);
        }
        if (idUnidade == null) {
            throw new EntidadeNaoEncontradaException("Conselheiro sem unidade vinculada");
        }
        List<TarefaUnidade> candidatos = tarefaUnidadeRepository.findByTarefaIdAndUnidadeId(idTarefa, idUnidade);
        TarefaUnidade tu = candidatos.stream()
                .filter(v -> v.getCiclo() != null && Boolean.TRUE.equals(v.getCiclo().getAtivo()))
                .findFirst()
                .or(() -> candidatos.stream().max(java.util.Comparator.comparing(TarefaUnidade::getId)))
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        "Tarefa " + idTarefa + " não está vinculada à sua unidade"));

        if (status == StatusKanban.CONCLUIDA) {
            throw new RequisicaoInvalidaException("Só a Diretoria conclui tarefas, ao aprovar a evidência");
        }
        if (tu.getStatusKanban() == StatusKanban.CONCLUIDA) {
            throw new RequisicaoInvalidaException("Tarefa já concluída não pode voltar no quadro");
        }

        tu.setStatusKanban(status);
        tarefaUnidadeRepository.save(tu);
        return TarefaMapper.toResponseDto(tu.getTarefa(), tu);
    }
}
