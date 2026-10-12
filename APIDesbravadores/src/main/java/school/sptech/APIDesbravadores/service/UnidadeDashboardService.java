package school.sptech.APIDesbravadores.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.Caderno;
import school.sptech.APIDesbravadores.domain.Ciclo;
import school.sptech.APIDesbravadores.domain.Desbravador;
import school.sptech.APIDesbravadores.domain.StatusKanban;
import school.sptech.APIDesbravadores.domain.Tarefa;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.dto.UnidadeDashboardDto;
import school.sptech.APIDesbravadores.dto.UnidadeDashboardDto.Atividade;
import school.sptech.APIDesbravadores.dto.UnidadeDashboardDto.Semana;
import school.sptech.APIDesbravadores.dto.UnidadeDashboardDto.UnidadeInfo;
import school.sptech.APIDesbravadores.exception.UnidadeNãoEncontradaException;
import school.sptech.APIDesbravadores.repository.CadernoRepository;
import school.sptech.APIDesbravadores.repository.CicloRepository;
import school.sptech.APIDesbravadores.repository.DesbravadorRepository;
import school.sptech.APIDesbravadores.repository.EvidenciaRepository;
import school.sptech.APIDesbravadores.repository.ResumoUnidadeViewRepository;
import school.sptech.APIDesbravadores.repository.TarefaUnidadeRepository;
import school.sptech.APIDesbravadores.repository.UnidadeRepository;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UnidadeDashboardService {

    static final int SEMANAS = 6;

    private final UnidadeRepository unidadeRepository;
    private final TarefaUnidadeRepository tarefaUnidadeRepository;
    private final CicloRepository cicloRepository;
    private final DesbravadorRepository desbravadorRepository;
    private final CadernoRepository cadernoRepository;
    private final EvidenciaRepository evidenciaRepository;
    private final ResumoUnidadeViewRepository resumoUnidadeViewRepository;
    private final Clock clock;

    @Autowired
    public UnidadeDashboardService(UnidadeRepository unidadeRepository, TarefaUnidadeRepository tarefaUnidadeRepository,
                                   CicloRepository cicloRepository, DesbravadorRepository desbravadorRepository,
                                   CadernoRepository cadernoRepository, EvidenciaRepository evidenciaRepository,
                                   ResumoUnidadeViewRepository resumoUnidadeViewRepository) {
        this(unidadeRepository, tarefaUnidadeRepository, cicloRepository, desbravadorRepository, cadernoRepository,
                evidenciaRepository, resumoUnidadeViewRepository, Clock.systemDefaultZone());
    }

    UnidadeDashboardService(UnidadeRepository unidadeRepository, TarefaUnidadeRepository tarefaUnidadeRepository,
                            CicloRepository cicloRepository, DesbravadorRepository desbravadorRepository,
                            CadernoRepository cadernoRepository, EvidenciaRepository evidenciaRepository,
                            ResumoUnidadeViewRepository resumoUnidadeViewRepository, Clock clock) {
        this.unidadeRepository = unidadeRepository;
        this.tarefaUnidadeRepository = tarefaUnidadeRepository;
        this.cicloRepository = cicloRepository;
        this.desbravadorRepository = desbravadorRepository;
        this.cadernoRepository = cadernoRepository;
        this.evidenciaRepository = evidenciaRepository;
        this.resumoUnidadeViewRepository = resumoUnidadeViewRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public UnidadeDashboardDto montar(Integer idUnidade, Integer idClubeUsuario) {
        Unidade unidade = unidadeRepository.findById(idUnidade)
                .filter(u -> u.getClube() != null && Objects.equals(u.getClube().getId(), idClubeUsuario))
                .orElseThrow(UnidadeNãoEncontradaException::new);
        LocalDate hoje = LocalDate.now(clock);

        // Mesmo recorte da vw_resumo_unidades: apenas o ciclo ativo do clube (quando existir).
        Integer idCicloAtivo = cicloRepository.findByClubeIdAndAtivoTrue(idClubeUsuario).map(Ciclo::getId).orElse(null);
        List<TarefaUnidade> vinculos = tarefaUnidadeRepository.findByUnidadeId(idUnidade).stream()
                .filter(tu -> tu.getTarefa() != null)
                .filter(tu -> idCicloAtivo == null || (tu.getCiclo() != null && idCicloAtivo.equals(tu.getCiclo().getId())))
                .toList();

        // ---- Semanas S1..S6 (segunda a domingo), terminando na semana atual ----
        LocalDate inicioSemanaAtual = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate inicioJanela = inicioSemanaAtual.minusWeeks(SEMANAS - 1);
        int[] ganhos = new int[SEMANAS];
        int[] descontos = new int[SEMANAS];

        int pontuacaoTotal = 0;
        int pontosCaderno = 0;
        for (TarefaUnidade tu : vinculos) {
            Tarefa t = tu.getTarefa();
            int previstos = t.getPontuacao() != null ? t.getPontuacao() : 0;
            int efetivos = tu.getPontuacaoConcedida() != null ? tu.getPontuacaoConcedida() : previstos;
            boolean concluida = tu.getStatusKanban() == StatusKanban.CONCLUIDA;

            if (concluida) {
                pontuacaoTotal += efetivos;
                if ("CADERNO".equalsIgnoreCase(t.getTipoTarefa())) {
                    pontosCaderno += efetivos;
                }
                if (tu.getDataConclusao() != null) {
                    int i = indiceSemana(inicioJanela, tu.getDataConclusao().toLocalDate());
                    if (i >= 0) {
                        ganhos[i] += efetivos;
                        descontos[i] += Math.max(0, previstos - efetivos);
                    }
                }
            } else {
                LocalDate prazo = prazo(tu);
                if (prazo != null && prazo.isBefore(hoje)) {
                    int i = indiceSemana(inicioJanela, prazo);
                    if (i >= 0) {
                        descontos[i] += previstos;
                    }
                }
            }
        }
        // A pontuação total oficial é a da view (mesmo número exibido no card da unidade).
        Integer daView = resumoUnidadeViewRepository.findByIdUnidade(idUnidade).map(v -> v.getPontuacao()).orElse(null);
        if (daView != null) {
            pontuacaoTotal = daView;
        }

        List<Semana> semanas = new ArrayList<>();
        for (int i = 0; i < SEMANAS; i++) {
            LocalDate ini = inicioJanela.plusWeeks(i);
            semanas.add(new Semana("S" + (i + 1), ini, ini.plusDays(6), ganhos[i], descontos[i]));
        }

        // ---- Evidências aguardando decisão da Diretoria ----
        List<Integer> idsEmRevisao = vinculos.stream()
                .filter(tu -> tu.getStatusKanban() == StatusKanban.EM_REVISAO)
                .map(TarefaUnidade::getId).toList();
        int evidenciasPendentes = idsEmRevisao.isEmpty() ? 0
                : (int) evidenciaRepository.countByUnidadeTarefaIdIn(idsEmRevisao);

        // ---- Atividades abertas (regra do protótipo para tarefas de caderno) ----
        Set<Integer> idadesDaUnidade = desbravadorRepository.findByUnidadeId(idUnidade).stream()
                .filter(d -> !Boolean.FALSE.equals(d.getAtivo()) && d.getDataNascimento() != null)
                .map(d -> Period.between(d.getDataNascimento(), hoje).getYears())
                .collect(Collectors.toSet());
        Map<Integer, Caderno> cadernos = cadernoRepository.findByClubeId(idClubeUsuario).stream()
                .collect(Collectors.toMap(Caderno::getId, Function.identity(), (a, b) -> a));

        List<Atividade> atividades = vinculos.stream()
                .filter(tu -> tu.getStatusKanban() != StatusKanban.CONCLUIDA)
                .filter(tu -> visivelParaUnidade(tu.getTarefa(), cadernos, idadesDaUnidade))
                .sorted(Comparator.comparing(UnidadeDashboardService::prazo, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(TarefaUnidade::getId))
                .map(tu -> new Atividade(
                        tu.getId(),
                        tu.getTarefa().getId(),
                        tu.getTarefa().getTitulo(),
                        tu.getTarefa().getTipoTarefa(),
                        tu.getTarefa().getCaderno() != null ? tu.getTarefa().getCaderno().getId() : null,
                        prazo(tu),
                        tu.getTarefa().getPontuacao() != null ? tu.getTarefa().getPontuacao() : 0,
                        tu.getStatusKanban() != null ? tu.getStatusKanban().getDescricao() : StatusKanban.A_FAZER.getDescricao()))
                .toList();

        String nomeConselheiro = resumoUnidadeViewRepository.findByIdUnidade(idUnidade).map(v -> v.getNomeConselheiro()).orElse(null);
        UnidadeInfo info = new UnidadeInfo(unidade.getId(), unidade.getNome(), unidade.getGenero(),
                unidade.getIdadeMinima(), unidade.getIdadeMaxima(), nomeConselheiro);

        return new UnidadeDashboardDto(info, pontuacaoTotal, pontosCaderno, semanas.get(SEMANAS - 1),
                evidenciasPendentes, semanas, atividades);
    }

    private static LocalDate prazo(TarefaUnidade tu) {
        if (tu.getPrazoEntrega() != null) {
            return tu.getPrazoEntrega().toLocalDate();
        }
        return tu.getTarefa() != null ? tu.getTarefa().getPrazoPadrao() : null;
    }

    private static int indiceSemana(LocalDate inicioJanela, LocalDate data) {
        long dias = ChronoUnit.DAYS.between(inicioJanela, data);
        if (dias < 0) {
            return -1;
        }
        int i = (int) (dias / 7);
        return i < SEMANAS ? i : -1;
    }

    /** Tarefa de caderno só aparece se a unidade tiver um desbravador com a idade-alvo do caderno. */
    private static boolean visivelParaUnidade(Tarefa t, Map<Integer, Caderno> cadernos, Set<Integer> idades) {
        if (!"CADERNO".equalsIgnoreCase(t.getTipoTarefa())) {
            return true;
        }
        if (t.getCaderno() == null) {
            return false;
        }
        Caderno c = cadernos.getOrDefault(t.getCaderno().getId(), t.getCaderno());
        return c.getIdadeAlvo() != null && idades.contains(c.getIdadeAlvo());
    }
}
