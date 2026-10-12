package school.sptech.APIDesbravadores.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.Ciclo;
import school.sptech.APIDesbravadores.domain.Penalidade;
import school.sptech.APIDesbravadores.domain.StatusKanban;
import school.sptech.APIDesbravadores.domain.Tarefa;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.dto.PenalidadeDto;
import school.sptech.APIDesbravadores.dto.UnidadeDashboardDto;
import school.sptech.APIDesbravadores.dto.UnidadeDashboardDto.Atividade;
import school.sptech.APIDesbravadores.dto.UnidadeDashboardDto.Semana;
import school.sptech.APIDesbravadores.dto.UnidadeDashboardDto.UnidadeInfo;
import school.sptech.APIDesbravadores.exception.UnidadeNãoEncontradaException;
import school.sptech.APIDesbravadores.repository.CicloRepository;
import school.sptech.APIDesbravadores.repository.DesbravadorCadernoRepository;
import school.sptech.APIDesbravadores.repository.EvidenciaRepository;
import school.sptech.APIDesbravadores.repository.PenalidadeRepository;
import school.sptech.APIDesbravadores.repository.ResumoUnidadeViewRepository;
import school.sptech.APIDesbravadores.repository.TarefaUnidadeRepository;
import school.sptech.APIDesbravadores.repository.UnidadeRepository;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Monta o dashboard da unidade: ganhos = pontos das tarefas concluídas na semana da conclusão;
 * descontos = penalidades lançadas pela Diretoria na semana do lançamento. Tudo no ciclo ativo.
 */
@Service
public class UnidadeDashboardService {

    static final int SEMANAS = 6;

    private final UnidadeRepository unidadeRepository;
    private final TarefaUnidadeRepository tarefaUnidadeRepository;
    private final CicloRepository cicloRepository;
    private final DesbravadorCadernoRepository vinculoRepository;
    private final PenalidadeRepository penalidadeRepository;
    private final EvidenciaRepository evidenciaRepository;
    private final ResumoUnidadeViewRepository resumoUnidadeViewRepository;
    private final Clock clock;

    @Autowired
    public UnidadeDashboardService(UnidadeRepository unidadeRepository, TarefaUnidadeRepository tarefaUnidadeRepository,
                                   CicloRepository cicloRepository, DesbravadorCadernoRepository vinculoRepository,
                                   PenalidadeRepository penalidadeRepository, EvidenciaRepository evidenciaRepository,
                                   ResumoUnidadeViewRepository resumoUnidadeViewRepository) {
        this(unidadeRepository, tarefaUnidadeRepository, cicloRepository, vinculoRepository, penalidadeRepository,
                evidenciaRepository, resumoUnidadeViewRepository, Clock.systemDefaultZone());
    }

    UnidadeDashboardService(UnidadeRepository unidadeRepository, TarefaUnidadeRepository tarefaUnidadeRepository,
                            CicloRepository cicloRepository, DesbravadorCadernoRepository vinculoRepository,
                            PenalidadeRepository penalidadeRepository, EvidenciaRepository evidenciaRepository,
                            ResumoUnidadeViewRepository resumoUnidadeViewRepository, Clock clock) {
        this.unidadeRepository = unidadeRepository;
        this.tarefaUnidadeRepository = tarefaUnidadeRepository;
        this.cicloRepository = cicloRepository;
        this.vinculoRepository = vinculoRepository;
        this.penalidadeRepository = penalidadeRepository;
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
        List<Penalidade> penalidades = idCicloAtivo == null ? List.of()
                : penalidadeRepository.findByUnidadeIdAndCicloIdOrderByDataRegistroDesc(idUnidade, idCicloAtivo);

        // ---- Semanas S1..S6 (segunda a domingo), terminando na semana atual ----
        LocalDate inicioSemanaAtual = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate inicioJanela = inicioSemanaAtual.minusWeeks(SEMANAS - 1);
        int[] ganhos = new int[SEMANAS];
        int[] descontos = new int[SEMANAS];

        int pontuacaoTotal = 0;
        int pontosCaderno = 0;
        for (TarefaUnidade tu : vinculos) {
            if (tu.getStatusKanban() != StatusKanban.CONCLUIDA) {
                continue;
            }
            Tarefa t = tu.getTarefa();
            int pontos = tu.getPontuacaoConcedida() != null ? tu.getPontuacaoConcedida()
                    : (t.getPontuacao() != null ? t.getPontuacao() : 0);
            pontuacaoTotal += pontos;
            if ("CADERNO".equalsIgnoreCase(t.getTipoTarefa())) {
                pontosCaderno += pontos;
            }
            if (tu.getDataConclusao() != null) {
                int i = indiceSemana(inicioJanela, tu.getDataConclusao().toLocalDate());
                if (i >= 0) {
                    ganhos[i] += pontos;
                }
            }
        }
        for (Penalidade p : penalidades) {
            pontuacaoTotal -= p.getPontos();
            if (p.getDataRegistro() != null) {
                int i = indiceSemana(inicioJanela, p.getDataRegistro().toLocalDate());
                if (i >= 0) {
                    descontos[i] += p.getPontos();
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

        // ---- Atividades abertas (tarefa de caderno só com criança vinculada ao caderno) ----
        Set<Integer> cadernosDaUnidade = RegraVisibilidadeCaderno.cadernosEmAndamento(
                vinculoRepository.findByDesbravadorUnidadeId(idUnidade));
        List<Atividade> atividades = vinculos.stream()
                .filter(tu -> tu.getStatusKanban() != StatusKanban.CONCLUIDA)
                .filter(tu -> RegraVisibilidadeCaderno.visivel(tu.getTarefa(), cadernosDaUnidade))
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
        List<PenalidadeDto> lista = penalidades.stream().map(PenalidadeService::toDto).toList();

        return new UnidadeDashboardDto(info, pontuacaoTotal, pontosCaderno, semanas.get(SEMANAS - 1),
                evidenciasPendentes, semanas, atividades, lista);
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
}
