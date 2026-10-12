package school.sptech.APIDesbravadores.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.dto.CheckinDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.exception.RequisicaoInvalidaException;
import school.sptech.APIDesbravadores.repository.CadernoRepository;
import school.sptech.APIDesbravadores.repository.ChecklistCadernoRepository;
import school.sptech.APIDesbravadores.repository.CicloRepository;
import school.sptech.APIDesbravadores.repository.DesbravadorCadernoRepository;
import school.sptech.APIDesbravadores.repository.DesbravadorRepository;
import school.sptech.APIDesbravadores.repository.EvidenciaRepository;
import school.sptech.APIDesbravadores.repository.ExecucaoCadernoRepository;
import school.sptech.APIDesbravadores.repository.TarefaRepository;
import school.sptech.APIDesbravadores.repository.TarefaUnidadeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Check-in das tarefas de caderno: transição automática do Kanban e conclusão do caderno por criança. */
class AcompanhamentoCadernoServiceTest {

    private TarefaUnidadeRepository tuRepo;
    private ChecklistCadernoRepository checklistRepo;
    private DesbravadorCadernoRepository vinculoRepo;
    private EvidenciaRepository evidenciaRepo;
    private AcompanhamentoCadernoService service;

    private final List<ChecklistCaderno> checklists = new ArrayList<>();
    private TarefaUnidade tu;
    private Tarefa tarefa;
    private Desbravador ana;
    private Desbravador bia;
    private DesbravadorCaderno vinculoAna;

    @BeforeEach
    void setUp() {
        tuRepo = mock(TarefaUnidadeRepository.class);
        checklistRepo = mock(ChecklistCadernoRepository.class);
        vinculoRepo = mock(DesbravadorCadernoRepository.class);
        evidenciaRepo = mock(EvidenciaRepository.class);
        ExecucaoCadernoRepository execRepo = mock(ExecucaoCadernoRepository.class);
        CicloRepository cicloRepo = mock(CicloRepository.class);
        TarefaRepository tarefaRepo = mock(TarefaRepository.class);
        service = new AcompanhamentoCadernoService(mock(CadernoRepository.class), mock(DesbravadorRepository.class),
                vinculoRepo, tarefaRepo, tuRepo, execRepo, checklistRepo, cicloRepo, evidenciaRepo);

        Unidade unidade = new Unidade();
        unidade.setId(2);
        Ciclo ciclo = new Ciclo();
        ciclo.setId(5);
        ciclo.setAtivo(true);
        Caderno caderno = new Caderno();
        caderno.setId(3);
        caderno.setNome("Amigo");
        tarefa = new Tarefa();
        tarefa.setId(40);
        tarefa.setTitulo("Nós e amarras");
        tarefa.setTipoTarefa("CADERNO");
        tarefa.setCaderno(caderno);
        tu = new TarefaUnidade();
        tu.setId(400);
        tu.setUnidade(unidade);
        tu.setTarefa(tarefa);
        tu.setCiclo(ciclo);
        tu.setStatusKanban(StatusKanban.A_FAZER);

        ana = crianca(1, "Ana", unidade);
        bia = crianca(2, "Bia", unidade);
        vinculoAna = vinculo(ana, caderno);
        when(vinculoRepo.findByCadernoId(3)).thenReturn(List.of(vinculoAna, vinculo(bia, caderno)));
        when(vinculoRepo.findByDesbravadorIdAndCadernoId(1, 3)).thenReturn(Optional.of(vinculoAna));
        when(cicloRepo.findByClubeIdAndAtivoTrue(10)).thenReturn(Optional.of(ciclo));
        when(tuRepo.findByTarefaIdAndUnidadeId(40, 2)).thenReturn(List.of(tu));
        // Caderno com um único requisito (a própria tarefa): cumprir = concluir o caderno.
        when(tarefaRepo.findByCadernoIdAndTipoTarefa(3, "CADERNO")).thenReturn(List.of(tarefa));

        ExecucaoCaderno exec = new ExecucaoCaderno();
        exec.setId(900);
        exec.setTarefa(tarefa);
        when(execRepo.findFirstByUnidadeIdAndTarefaIdAndCicloIdOrderByIdAsc(2, 40, 5)).thenReturn(Optional.of(exec));
        when(checklistRepo.findByExecucaoCadernoId(900)).thenReturn(checklists);
        when(checklistRepo.findFirstByExecucaoCadernoIdAndDesbravadorId(eq(900), anyInt())).thenAnswer(inv ->
                checklists.stream().filter(c -> c.getDesbravador().getId().equals(inv.getArgument(1))).findFirst());
        when(checklistRepo.save(any())).thenAnswer(inv -> {
            ChecklistCaderno c = inv.getArgument(0);
            if (!checklists.contains(c)) checklists.add(c);
            return c;
        });
        when(checklistRepo.findByDesbravadorIdAndConcluiuTarefaTrueAndExecucaoCadernoTarefaCadernoId(anyInt(), eq(3)))
                .thenAnswer(inv -> checklists.stream()
                        .filter(c -> c.getDesbravador().getId().equals(inv.getArgument(0)) && c.getConcluiuTarefa())
                        .toList());
    }

    private static Desbravador crianca(int id, String nome, Unidade unidade) {
        Desbravador d = new Desbravador();
        d.setId(id);
        d.setNome(nome);
        d.setAtivo(true);
        d.setUnidade(unidade);
        return d;
    }

    private static DesbravadorCaderno vinculo(Desbravador d, Caderno c) {
        DesbravadorCaderno v = new DesbravadorCaderno();
        v.setDesbravador(d);
        v.setCaderno(c);
        return v;
    }

    @Test
    void marcarParteVaiParaAndamentoEMarcarTodasVaiParaRevisaoComPendencia() {
        CheckinDto parcial = service.marcarCheckin(40, 1, true, 2, 10);
        assertEquals("EM ANDAMENTO", parcial.statusKanban());
        assertEquals(1, parcial.marcados());
        assertNotNull(vinculoAna.getDataConclusao(), "Ana cumpriu o único requisito: caderno concluído");

        CheckinDto completo = service.marcarCheckin(40, 2, true, 2, 10);
        assertEquals("EM REVISAO", completo.statusKanban());
        assertEquals(2, completo.marcados());
        verify(evidenciaRepo).save(any(Evidencia.class));
    }

    @Test
    void desmarcarVoltaParaAndamentoRetiraPendenciaEDesfazConclusao() {
        service.marcarCheckin(40, 1, true, 2, 10);
        service.marcarCheckin(40, 2, true, 2, 10);
        Evidencia pendencia = new Evidencia();
        pendencia.setUrlS3(AcompanhamentoCadernoService.URL_CHECKIN);
        when(evidenciaRepo.findByUnidadeTarefaIdIn(anyCollection())).thenReturn(List.of(pendencia));

        CheckinDto dto = service.marcarCheckin(40, 1, false, 2, 10);

        assertEquals("EM ANDAMENTO", dto.statusKanban());
        verify(evidenciaRepo).delete(pendencia);
        assertNull(vinculoAna.getDataConclusao());
    }

    @Test
    void naoMexeEmTarefaConcluidaNemEmCriancaForaDoCaderno() {
        assertThrows(EntidadeNaoEncontradaException.class, () -> service.marcarCheckin(40, 99, true, 2, 10));
        tu.setStatusKanban(StatusKanban.CONCLUIDA);
        assertThrows(RequisicaoInvalidaException.class, () -> service.marcarCheckin(40, 1, true, 2, 10));
        verify(checklistRepo, never()).save(any());
    }

    @Test
    void tarefaDeClubeNaoTemCheckin() {
        tarefa.setTipoTarefa("CLUBE");
        assertThrows(RequisicaoInvalidaException.class, () -> service.obterCheckin(40, 2, 10));
    }
}
