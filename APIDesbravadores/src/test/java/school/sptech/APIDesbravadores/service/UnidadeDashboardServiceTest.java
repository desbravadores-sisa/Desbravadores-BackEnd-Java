package school.sptech.APIDesbravadores.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.sptech.APIDesbravadores.domain.Caderno;
import school.sptech.APIDesbravadores.domain.Ciclo;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.Desbravador;
import school.sptech.APIDesbravadores.domain.StatusKanban;
import school.sptech.APIDesbravadores.domain.Tarefa;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.dto.UnidadeDashboardDto;
import school.sptech.APIDesbravadores.exception.UnidadeNãoEncontradaException;
import school.sptech.APIDesbravadores.repository.CadernoRepository;
import school.sptech.APIDesbravadores.repository.CicloRepository;
import school.sptech.APIDesbravadores.repository.DesbravadorRepository;
import school.sptech.APIDesbravadores.repository.EvidenciaRepository;
import school.sptech.APIDesbravadores.repository.ResumoUnidadeViewRepository;
import school.sptech.APIDesbravadores.repository.TarefaUnidadeRepository;
import school.sptech.APIDesbravadores.repository.UnidadeRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private UnidadeRepository unidadeRepo;
    private TarefaUnidadeRepository tuRepo;
    private CicloRepository cicloRepo;
    private DesbravadorRepository desbravadorRepo;
    private CadernoRepository cadernoRepo;
    private EvidenciaRepository evidenciaRepo;
    private ResumoUnidadeViewRepository viewRepo;
    private UnidadeDashboardService service;

    private Clube clube;
    private Unidade unidade;
    private Ciclo ciclo;
    private Caderno caderno12;
    private int seq = 1;

    @BeforeEach
    void setUp() {
        unidadeRepo = mock(UnidadeRepository.class);
        tuRepo = mock(TarefaUnidadeRepository.class);
        cicloRepo = mock(CicloRepository.class);
        desbravadorRepo = mock(DesbravadorRepository.class);
        cadernoRepo = mock(CadernoRepository.class);
        evidenciaRepo = mock(EvidenciaRepository.class);
        viewRepo = mock(ResumoUnidadeViewRepository.class);
        Clock clock = Clock.fixed(HOJE.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        service = new UnidadeDashboardService(unidadeRepo, tuRepo, cicloRepo, desbravadorRepo, cadernoRepo,
                evidenciaRepo, viewRepo, clock);

        clube = new Clube();
        clube.setId(1);
        unidade = new Unidade();
        unidade.setId(10);
        unidade.setNome("Tigresas");
        unidade.setGenero("Feminino");
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
        when(cadernoRepo.findByClubeId(1)).thenReturn(List.of(caderno12));
        when(viewRepo.findByIdUnidade(10)).thenReturn(Optional.empty());
    }

    private TarefaUnidade vinculo(String tipo, int pontos, StatusKanban status, LocalDate prazo,
                                  LocalDate conclusao, Integer concedida, Caderno caderno, Ciclo cicloDoVinculo) {
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
        tu.setPrazoEntrega(prazo != null ? prazo.atTime(23, 59) : null);
        tu.setDataConclusao(conclusao != null ? conclusao.atTime(10, 0) : null);
        tu.setPontuacaoConcedida(concedida);
        return tu;
    }

    private Desbravador desbravadorComIdade(int anos) {
        Desbravador d = new Desbravador();
        d.setDataNascimento(HOJE.minusYears(anos).minusDays(1));
        d.setAtivo(true);
        return d;
    }

    @Test
    void calculaCardsSemanasEAtividades() {
        List<TarefaUnidade> vinculos = List.of(
                // S6 (12/10): concluída com 80 de 100 -> ganhos 80, descontos 20
                vinculo("GERAL", 100, StatusKanban.CONCLUIDA, null, LocalDate.of(2026, 10, 13), 80, null, ciclo),
                // S5 (05/10): concluída sem nota -> vale a pontuação prevista
                vinculo("CADERNO", 50, StatusKanban.CONCLUIDA, null, LocalDate.of(2026, 10, 6), null, caderno12, ciclo),
                // S4 (28/09): prazo vencido sem conclusão -> desconto de 30
                vinculo("GERAL", 30, StatusKanban.A_FAZER, LocalDate.of(2026, 10, 1), null, null, null, ciclo),
                // Em revisão, prazo futuro -> atividade aberta + pendência
                vinculo("GERAL", 40, StatusKanban.EM_REVISAO, LocalDate.of(2026, 10, 20), null, null, null, ciclo),
                // Caderno de 12 anos: a unidade tem desbravador de 12 -> aparece
                vinculo("CADERNO", 25, StatusKanban.EM_ANDAMENTO, LocalDate.of(2026, 10, 25), null, null, caderno12, ciclo),
                // Outro ciclo -> ignorada em tudo
                vinculo("GERAL", 999, StatusKanban.CONCLUIDA, null, LocalDate.of(2026, 10, 13), null, null, new Ciclo())
        );
        when(tuRepo.findByUnidadeId(10)).thenReturn(vinculos);
        when(desbravadorRepo.findByUnidadeId(10)).thenReturn(List.of(desbravadorComIdade(12)));
        when(evidenciaRepo.countByUnidadeTarefaIdIn(anyCollection())).thenReturn(2L);

        UnidadeDashboardDto dto = service.montar(10, 1);

        assertEquals(130, dto.pontuacaoTotal());
        assertEquals(50, dto.pontosCaderno());
        assertEquals(2, dto.evidenciasPendentes());
        assertEquals(6, dto.semanas().size());
        assertEquals(LocalDate.of(2026, 9, 7), dto.semanas().get(0).inicio());
        assertEquals("S6", dto.semanaAtual().rotulo());
        assertEquals(80, dto.semanaAtual().ganhos());
        assertEquals(20, dto.semanaAtual().descontos());
        assertEquals(50, dto.semanas().get(4).ganhos());
        assertEquals(30, dto.semanas().get(3).descontos());
        assertEquals(3, dto.atividades().size());
        assertEquals("EM REVISAO", dto.atividades().get(1).status());
        assertEquals(12, dto.unidade().idadeMaxima());
    }

    @Test
    void escondeTarefaDeCadernoSemDesbravadorDaIdade() {
        when(tuRepo.findByUnidadeId(10)).thenReturn(List.of(
                vinculo("CADERNO", 25, StatusKanban.A_FAZER, LocalDate.of(2026, 10, 25), null, null, caderno12, ciclo)));
        when(desbravadorRepo.findByUnidadeId(10)).thenReturn(List.of(desbravadorComIdade(10)));

        assertEquals(0, service.montar(10, 1).atividades().size());
    }

    @Test
    void unidadeDeOutroClubeDa404() {
        assertThrows(UnidadeNãoEncontradaException.class, () -> service.montar(10, 2));
    }
}
