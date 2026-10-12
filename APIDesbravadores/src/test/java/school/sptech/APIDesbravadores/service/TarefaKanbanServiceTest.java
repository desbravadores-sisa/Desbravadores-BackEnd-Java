package school.sptech.APIDesbravadores.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.sptech.APIDesbravadores.domain.Caderno;
import school.sptech.APIDesbravadores.domain.Ciclo;
import school.sptech.APIDesbravadores.domain.Desbravador;
import school.sptech.APIDesbravadores.domain.DesbravadorCaderno;
import school.sptech.APIDesbravadores.domain.Evidencia;
import school.sptech.APIDesbravadores.domain.StatusKanban;
import school.sptech.APIDesbravadores.domain.Tarefa;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.dto.TarefaResponseDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.exception.RequisicaoInvalidaException;
import school.sptech.APIDesbravadores.repository.CadernoRepository;
import school.sptech.APIDesbravadores.repository.CicloRepository;
import school.sptech.APIDesbravadores.repository.ClubeRepository;
import school.sptech.APIDesbravadores.repository.DesbravadorCadernoRepository;
import school.sptech.APIDesbravadores.repository.EvidenciaRepository;
import school.sptech.APIDesbravadores.repository.TarefaRepository;
import school.sptech.APIDesbravadores.repository.TarefaUnidadeRepository;
import school.sptech.APIDesbravadores.repository.UnidadeRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TarefaKanbanServiceTest {

    private TarefaUnidadeRepository tuRepo;
    private CicloRepository cicloRepo;
    private CadernoRepository cadernoRepo;
    private DesbravadorCadernoRepository vinculoRepo;
    private EvidenciaRepository evidenciaRepo;
    private TarefaService service;

    private Unidade unidade;
    private Ciclo ciclo;
    private Caderno caderno12;
    private int seq = 1;

    @BeforeEach
    void setUp() {
        tuRepo = mock(TarefaUnidadeRepository.class);
        cicloRepo = mock(CicloRepository.class);
        cadernoRepo = mock(CadernoRepository.class);
        vinculoRepo = mock(DesbravadorCadernoRepository.class);
        evidenciaRepo = mock(EvidenciaRepository.class);
        service = new TarefaService(mock(TarefaRepository.class), tuRepo, mock(ClubeRepository.class),
                mock(UnidadeRepository.class), cicloRepo, cadernoRepo, vinculoRepo, evidenciaRepo);

        unidade = new Unidade();
        unidade.setId(2);
        ciclo = new Ciclo();
        ciclo.setId(5);
        ciclo.setAtivo(true);
        caderno12 = new Caderno();
        caderno12.setId(3);
        caderno12.setIdadeAlvo(12);

        when(cicloRepo.findByClubeIdAndAtivoTrue(10)).thenReturn(Optional.of(ciclo));
        when(cadernoRepo.findByClubeId(10)).thenReturn(List.of(caderno12));
        // A unidade tem uma criança vinculada a OUTRO caderno (id 99), não ao caderno12.
        Caderno outro = new Caderno();
        outro.setId(99);
        Desbravador d = new Desbravador();
        d.setAtivo(true);
        DesbravadorCaderno v = new DesbravadorCaderno();
        v.setDesbravador(d);
        v.setCaderno(outro);
        when(vinculoRepo.findByDesbravadorUnidadeId(2)).thenReturn(List.of(v));
    }

    private TarefaUnidade vinculo(String tipo, StatusKanban status, Caderno caderno, Ciclo c) {
        Tarefa t = new Tarefa();
        t.setId(seq);
        t.setTitulo("Tarefa " + seq);
        t.setTipoTarefa(tipo);
        t.setPontuacao(10);
        t.setCaderno(caderno);
        t.setPrazoPadrao(LocalDate.of(2026, 11, seq));
        TarefaUnidade tu = new TarefaUnidade();
        tu.setId(100 + seq++);
        tu.setUnidade(unidade);
        tu.setTarefa(t);
        tu.setCiclo(c);
        tu.setStatusKanban(status);
        return tu;
    }

    @Test
    void kanbanTrazSoVinculosDaUnidadeNoCicloAtivoComAsQuatroColunas() {
        TarefaUnidade clube = vinculo("CLUBE", StatusKanban.EM_REVISAO, null, ciclo);
        TarefaUnidade cadernoSemCrianca = vinculo("CADERNO", StatusKanban.A_FAZER, caderno12, ciclo);
        Ciclo antigo = new Ciclo();
        antigo.setId(1);
        TarefaUnidade cicloAntigo = vinculo("CLUBE", StatusKanban.A_FAZER, null, antigo);
        TarefaUnidade semStatus = vinculo("CLUBE", null, null, ciclo);
        TarefaUnidade cadernoConcluido = vinculo("CADERNO", StatusKanban.CONCLUIDA, caderno12, ciclo);
        when(tuRepo.findByUnidadeId(2)).thenReturn(List.of(clube, cadernoSemCrianca, cicloAntigo, semStatus, cadernoConcluido));
        Evidencia ev = new Evidencia();
        ev.setUnidadeTarefa(clube);
        when(evidenciaRepo.findByUnidadeTarefaIdIn(anyCollection())).thenReturn(List.of(ev));

        Map<String, List<TarefaResponseDto>> kanban = service.getKanbanDaUnidade(2, 10);

        assertEquals(List.of("A FAZER", "EM ANDAMENTO", "EM REVISAO", "CONCLUIDA"), List.copyOf(kanban.keySet()));
        assertEquals(1, kanban.get("EM REVISAO").size());
        assertTrue(kanban.get("EM REVISAO").get(0).getPossuiEvidencia());
        // só a tarefa sem status (vira A FAZER); a de caderno some (nenhuma criança vinculada ao caderno) e a do ciclo antigo também
        assertEquals(1, kanban.get("A FAZER").size());
        assertEquals(semStatus.getId(), kanban.get("A FAZER").get(0).getIdUnidadeTarefa());
        assertEquals(LocalDate.of(2026, 11, 4), kanban.get("A FAZER").get(0).getPrazoEntrega());
        // concluída de caderno continua no quadro mesmo sem criança vinculada (pontos já conquistados)
        assertEquals(1, kanban.get("CONCLUIDA").size());
    }

    @Test
    void moverAceitaRotuloComAcentoEAlteraOVinculoDaUnidade() {
        TarefaUnidade tu = vinculo("CLUBE", StatusKanban.A_FAZER, null, ciclo);
        tu.setPrazoEntrega(LocalDateTime.of(2026, 11, 20, 23, 59));
        when(tuRepo.findByTarefaIdAndUnidadeId(1, 2)).thenReturn(List.of(tu));

        TarefaResponseDto dto = service.updateStatusDaUnidade(1, 2, "em andamento");

        assertEquals(StatusKanban.EM_ANDAMENTO, tu.getStatusKanban());
        assertEquals("EM ANDAMENTO", dto.getStatusKanban());
        verify(tuRepo).save(tu);
    }

    @Test
    void revisaoSoPorEvidenciaOuCheckinENaoSaiDeRevisaoArrastando() {
        TarefaUnidade aberta = vinculo("CLUBE", StatusKanban.EM_ANDAMENTO, null, ciclo);
        TarefaUnidade emRevisao = vinculo("CLUBE", StatusKanban.EM_REVISAO, null, ciclo);
        when(tuRepo.findByTarefaIdAndUnidadeId(1, 2)).thenReturn(List.of(aberta));
        when(tuRepo.findByTarefaIdAndUnidadeId(2, 2)).thenReturn(List.of(emRevisao));

        RequisicaoInvalidaException e = assertThrows(RequisicaoInvalidaException.class,
                () -> service.updateStatusDaUnidade(1, 2, "Em revisão"));
        assertTrue(e.getMessage().contains("evidência"));
        assertThrows(RequisicaoInvalidaException.class, () -> service.updateStatusDaUnidade(2, 2, "A fazer"));
        verify(tuRepo, never()).save(any());
    }

    @Test
    void conselheiroNaoConcluiNemReabreTarefaConcluida() {
        TarefaUnidade aberta = vinculo("CLUBE", StatusKanban.EM_REVISAO, null, ciclo);
        TarefaUnidade concluida = vinculo("CLUBE", StatusKanban.CONCLUIDA, null, ciclo);
        when(tuRepo.findByTarefaIdAndUnidadeId(1, 2)).thenReturn(List.of(aberta));
        when(tuRepo.findByTarefaIdAndUnidadeId(2, 2)).thenReturn(List.of(concluida));

        assertThrows(RequisicaoInvalidaException.class, () -> service.updateStatusDaUnidade(1, 2, "Concluído"));
        assertThrows(RequisicaoInvalidaException.class, () -> service.updateStatusDaUnidade(2, 2, "A fazer"));
        assertThrows(RequisicaoInvalidaException.class, () -> service.updateStatusDaUnidade(1, 2, "qualquer"));
        verify(tuRepo, never()).save(any());
    }

    @Test
    void tarefaDeOutraUnidadeDa404() {
        when(tuRepo.findByTarefaIdAndUnidadeId(9, 2)).thenReturn(List.of());
        assertThrows(EntidadeNaoEncontradaException.class, () -> service.updateStatusDaUnidade(9, 2, "Em andamento"));
    }
}
