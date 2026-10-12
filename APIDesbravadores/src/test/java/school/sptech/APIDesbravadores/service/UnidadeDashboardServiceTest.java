package school.sptech.APIDesbravadores.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.sptech.APIDesbravadores.domain.Caderno;
import school.sptech.APIDesbravadores.domain.Ciclo;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.Desbravador;
import school.sptech.APIDesbravadores.domain.DesbravadorCaderno;
import school.sptech.APIDesbravadores.domain.Penalidade;
import school.sptech.APIDesbravadores.domain.StatusKanban;
import school.sptech.APIDesbravadores.domain.Tarefa;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.dto.UnidadeDashboardDto;
import school.sptech.APIDesbravadores.exception.UnidadeNãoEncontradaException;
import school.sptech.APIDesbravadores.repository.CicloRepository;
import school.sptech.APIDesbravadores.repository.DesbravadorCadernoRepository;
import school.sptech.APIDesbravadores.repository.EvidenciaRepository;
import school.sptech.APIDesbravadores.repository.PenalidadeRepository;
import school.sptech.APIDesbravadores.repository.ResumoUnidadeViewRepository;
import school.sptech.APIDesbravadores.repository.TarefaUnidadeRepository;
import school.sptech.APIDesbravadores.repository.UnidadeRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UnidadeDashboardServiceTest {

    // Quarta-feira, 14/10/2026 -> semana atual (S6) = 12/10 a 18/10; S1 começa em 07/09.
    private static final LocalDate HOJE = LocalDate.of(2026, 10, 14);

    private TarefaUnidadeRepository tuRepo;
    private DesbravadorCadernoRepository vinculoRepo;
    private PenalidadeRepository penalidadeRepo;
    private EvidenciaRepository evidenciaRepo;
    private UnidadeDashboardService service;

    private Unidade unidade;
    private Ciclo ciclo;
    private Caderno caderno12;
    private int seq = 1;

    @BeforeEach
    void setUp() {
        UnidadeRepository unidadeRepo = mock(UnidadeRepository.class);
        tuRepo = mock(TarefaUnidadeRepository.class);
        CicloRepository cicloRepo = mock(CicloRepository.class);
        vinculoRepo = mock(DesbravadorCadernoRepository.class);
        penalidadeRepo = mock(PenalidadeRepository.class);
        evidenciaRepo = mock(EvidenciaRepository.class);
        ResumoUnidadeViewRepository viewRepo = mock(ResumoUnidadeViewRepository.class);
        Clock clock = Clock.fixed(HOJE.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        service = new UnidadeDashboardService(unidadeRepo, tuRepo, cicloRepo, vinculoRepo, penalidadeRepo,
                evidenciaRepo, viewRepo, clock);

        Clube clube = new Clube();
        clube.setId(1);
        unidade = new Unidade();
        unidade.setId(10);
        unidade.setNome("Tigresas");
        unidade.setIdadeMinima(10);
        unidade.setIdadeMaxima(12);
        unidade.setClube(clube);
        ciclo = new Ciclo();
        ciclo.setId(5);
        caderno12 = new Caderno();
        caderno12.setId(3);
        caderno12.setIdadeAlvo(12);

        when(unidadeRepo.findById(10)).thenReturn(Optional.of(unidade));
        when(cicloRepo.findByClubeIdAndAtivoTrue(1)).thenReturn(Optional.of(ciclo));
        when(viewRepo.findByIdUnidade(10)).thenReturn(Optional.empty());
        when(vinculoRepo.findByDesbravadorUnidadeId(10)).thenReturn(List.of());
        when(penalidadeRepo.findByUnidadeIdAndCicloIdOrderByDataRegistroDesc(10, 5)).thenReturn(List.of());
    }

    private TarefaUnidade vinculo(String tipo, int pontos, StatusKanban status, LocalDate conclusao,
                                  Integer concedida, Caderno caderno, Ciclo cicloDoVinculo) {
        Tarefa t = new Tarefa();
        t.setId(seq);
        t.setTitulo("Tarefa " + seq);
        t.setTipoTarefa(tipo);
        t.setPontuacao(pontos);
        t.setCaderno(caderno);
        TarefaUnidade tu = new TarefaUnidade();
        tu.setId(100 + seq++);
        tu.setUnidade(unidade);
        tu.setTarefa(t);
        tu.setCiclo(cicloDoVinculo);
        tu.setStatusKanban(status);
        tu.setPrazoEntrega(LocalDate.of(2026, 10, 20).atTime(23, 59));
        tu.setDataConclusao(conclusao != null ? conclusao.atTime(10, 0) : null);
        tu.setPontuacaoConcedida(concedida);
        return tu;
    }

    private Penalidade penalidade(int pontos, LocalDate data) {
        Penalidade p = new Penalidade();
        p.setId(seq++);
        p.setUnidade(unidade);
        p.setCiclo(ciclo);
        p.setPontos(pontos);
        p.setMotivo("Atraso");
        p.setDataRegistro(data.atTime(9, 0));
        return p;
    }

    private DesbravadorCaderno vinculoCrianca(Caderno caderno) {
        Desbravador d = new Desbravador();
        d.setId(77);
        d.setAtivo(true);
        DesbravadorCaderno v = new DesbravadorCaderno();
        v.setDesbravador(d);
        v.setCaderno(caderno);
        return v;
    }

    @Test
    void ganhosDasConclusoesEDescontosDasPenalidades() {
        when(tuRepo.findByUnidadeId(10)).thenReturn(List.of(
                vinculo("CLUBE", 100, StatusKanban.CONCLUIDA, LocalDate.of(2026, 10, 13), 80, null, ciclo),
                vinculo("CADERNO", 50, StatusKanban.CONCLUIDA, LocalDate.of(2026, 10, 6), null, caderno12, ciclo),
                vinculo("CLUBE", 40, StatusKanban.EM_REVISAO, null, null, null, ciclo),
                vinculo("CADERNO", 25, StatusKanban.EM_ANDAMENTO, null, null, caderno12, ciclo),
                vinculo("CLUBE", 999, StatusKanban.CONCLUIDA, LocalDate.of(2026, 10, 13), null, null, new Ciclo())));
        when(penalidadeRepo.findByUnidadeIdAndCicloIdOrderByDataRegistroDesc(10, 5)).thenReturn(List.of(
                penalidade(15, LocalDate.of(2026, 10, 12)), penalidade(10, LocalDate.of(2026, 9, 29))));
        when(vinculoRepo.findByDesbravadorUnidadeId(10)).thenReturn(List.of(vinculoCrianca(caderno12)));
        when(evidenciaRepo.countByUnidadeTarefaIdIn(anyCollection())).thenReturn(1L);

        UnidadeDashboardDto dto = service.montar(10, 1);

        assertEquals(80 + 50 - 15 - 10, dto.pontuacaoTotal());
        assertEquals(50, dto.pontosCaderno());
        assertEquals(1, dto.evidenciasPendentes());
        assertEquals("S6", dto.semanaAtual().rotulo());
        assertEquals(80, dto.semanaAtual().ganhos());
        assertEquals(15, dto.semanaAtual().descontos());
        assertEquals(50, dto.semanas().get(4).ganhos());
        assertEquals(10, dto.semanas().get(3).descontos());
        assertEquals(2, dto.penalidades().size());
        assertEquals(2, dto.atividades().size());
    }

    @Test
    void escondeTarefaDeCadernoSemCriancaVinculada() {
        when(tuRepo.findByUnidadeId(10)).thenReturn(List.of(
                vinculo("CADERNO", 25, StatusKanban.A_FAZER, null, null, caderno12, ciclo)));
        assertEquals(0, service.montar(10, 1).atividades().size());
    }

    @Test
    void unidadeDeOutroClubeDa404() {
        assertThrows(UnidadeNãoEncontradaException.class, () -> service.montar(10, 2));
    }
}
