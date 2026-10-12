package school.sptech.APIDesbravadores.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import school.sptech.APIDesbravadores.domain.Caderno;
import school.sptech.APIDesbravadores.domain.Ciclo;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.Evidencia;
import school.sptech.APIDesbravadores.domain.ExecucaoCaderno;
import school.sptech.APIDesbravadores.domain.StatusKanban;
import school.sptech.APIDesbravadores.domain.Tarefa;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;
import school.sptech.APIDesbravadores.dto.TarefaCreateDto;
import school.sptech.APIDesbravadores.dto.TarefaResponseDto;
import school.sptech.APIDesbravadores.dto.TarefaUpdateDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.exception.RequisicaoInvalidaException;
import school.sptech.APIDesbravadores.repository.CadernoRepository;
import school.sptech.APIDesbravadores.repository.ChecklistCadernoRepository;
import school.sptech.APIDesbravadores.repository.CicloRepository;
import school.sptech.APIDesbravadores.repository.ClubeRepository;
import school.sptech.APIDesbravadores.repository.DesbravadorCadernoRepository;
import school.sptech.APIDesbravadores.repository.EvidenciaRepository;
import school.sptech.APIDesbravadores.repository.ExecucaoCadernoRepository;
import school.sptech.APIDesbravadores.repository.TarefaRepository;
import school.sptech.APIDesbravadores.repository.TarefaUnidadeRepository;
import school.sptech.APIDesbravadores.repository.UnidadeRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Regras da tela "Gerenciar Tarefas" (Diretoria). */
class TarefaGerenciarServiceTest {

    private TarefaRepository tarefaRepo;
    private TarefaUnidadeRepository tuRepo;
    private CicloRepository cicloRepo;
    private CadernoRepository cadernoRepo;
    private EvidenciaRepository evidenciaRepo;
    private ExecucaoCadernoRepository execRepo;
    private ChecklistCadernoRepository checklistRepo;
    private ArmazenamentoArquivoService armazenamento;
    private TarefaService service;

    private Clube clube;
    private Ciclo ativo;

    @BeforeEach
    void setUp() {
        tarefaRepo = mock(TarefaRepository.class);
        tuRepo = mock(TarefaUnidadeRepository.class);
        cicloRepo = mock(CicloRepository.class);
        cadernoRepo = mock(CadernoRepository.class);
        evidenciaRepo = mock(EvidenciaRepository.class);
        execRepo = mock(ExecucaoCadernoRepository.class);
        checklistRepo = mock(ChecklistCadernoRepository.class);
        armazenamento = mock(ArmazenamentoArquivoService.class);
        ClubeRepository clubeRepo = mock(ClubeRepository.class);
        service = new TarefaService(tarefaRepo, tuRepo, clubeRepo, mock(UnidadeRepository.class), cicloRepo,
                cadernoRepo, mock(DesbravadorCadernoRepository.class), evidenciaRepo);
        ReflectionTestUtils.setField(service, "execucaoCadernoRepository", execRepo);
        ReflectionTestUtils.setField(service, "checklistCadernoRepository", checklistRepo);
        ReflectionTestUtils.setField(service, "armazenamento", armazenamento);

        clube = new Clube();
        clube.setId(1);
        ativo = new Ciclo();
        ativo.setId(5);
        when(clubeRepo.findById(1)).thenReturn(Optional.of(clube));
        when(cicloRepo.findByClubeIdAndAtivoTrue(1)).thenReturn(Optional.of(ativo));
    }

    private Tarefa tarefa(int id, Clube dono) {
        Tarefa t = new Tarefa();
        t.setId(id);
        t.setTitulo("Tarefa " + id);
        t.setTipoTarefa("CLUBE");
        t.setClube(dono);
        return t;
    }

    private TarefaUnidade vinculo(int id, Tarefa t, Ciclo ciclo, StatusKanban status) {
        TarefaUnidade tu = new TarefaUnidade();
        tu.setId(id);
        tu.setTarefa(t);
        tu.setCiclo(ciclo);
        tu.setStatusKanban(status);
        return tu;
    }

    private static TarefaCreateDto criacao(String tipo, Integer fkCaderno, Integer pontos) {
        TarefaCreateDto dto = new TarefaCreateDto();
        dto.setFkClube(1);
        dto.setTitulo("Nova");
        dto.setTipoTarefa(tipo);
        dto.setFkCaderno(fkCaderno);
        dto.setPontuacao(pontos);
        return dto;
    }

    @Test
    void listagemContaEntregasSoDoCicloAtivo() {
        Tarefa t = tarefa(10, clube);
        Ciclo antigo = new Ciclo();
        antigo.setId(4);
        when(tarefaRepo.findByClubeId(1)).thenReturn(List.of(t));
        when(tuRepo.findByTarefaIdIn(any())).thenReturn(List.of(
                vinculo(1, t, ativo, StatusKanban.CONCLUIDA),
                vinculo(2, t, ativo, StatusKanban.EM_REVISAO),
                vinculo(3, t, ativo, StatusKanban.EM_ANDAMENTO),
                vinculo(4, t, ativo, StatusKanban.A_FAZER),
                vinculo(5, t, antigo, StatusKanban.CONCLUIDA)));

        TarefaResponseDto dto = service.findAllByClube(1).get(0);

        assertEquals(4, dto.getUnidadesTotal());
        assertEquals(2, dto.getUnidadesEntregaram());
    }

    @Test
    void criacaoValidaTipoCadernoEPontuacao() {
        assertThrows(RequisicaoInvalidaException.class, () -> service.create(criacao("CADERNO", null, 10)));
        assertThrows(RequisicaoInvalidaException.class, () -> service.create(criacao("CLUBE", null, -5)));
        assertThrows(RequisicaoInvalidaException.class, () -> service.create(criacao("OUTRO", null, 5)));

        Clube outro = new Clube();
        outro.setId(2);
        Caderno deOutroClube = new Caderno();
        deOutroClube.setId(9);
        deOutroClube.setClube(outro);
        when(cadernoRepo.findById(9)).thenReturn(Optional.of(deOutroClube));
        assertThrows(EntidadeNaoEncontradaException.class, () -> service.create(criacao("CADERNO", 9, 10)));
        verify(tarefaRepo, never()).save(any());
    }

    @Test
    void tarefaDeClubeIgnoraCadernoInformado() {
        when(tarefaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        TarefaCreateDto dto = criacao("clube", 3, 10);

        TarefaResponseDto criada = service.create(dto);

        assertEquals("CLUBE", criada.getTipoTarefa());
        assertEquals(null, criada.getFkCaderno());
    }

    @Test
    void naoEditaNemExcluiTarefaDeOutroClube() {
        Clube outro = new Clube();
        outro.setId(2);
        when(tarefaRepo.findById(7)).thenReturn(Optional.of(tarefa(7, outro)));
        TarefaUpdateDto dto = new TarefaUpdateDto();
        dto.setTitulo("x");

        assertThrows(EntidadeNaoEncontradaException.class, () -> service.update(7, dto, 1));
        assertThrows(EntidadeNaoEncontradaException.class, () -> service.delete(7, 1));
        verify(tarefaRepo, never()).delete(any());
    }

    @Test
    void excluirRemoveEvidenciasArquivosCheckinsEVinculos() {
        Tarefa t = tarefa(10, clube);
        when(tarefaRepo.findById(10)).thenReturn(Optional.of(t));
        when(tuRepo.findByTarefaIdIn(List.of(10))).thenReturn(List.of(vinculo(1, t, ativo, StatusKanban.EM_REVISAO)));
        Evidencia ev = new Evidencia();
        ev.setUrlS3("/arquivos/abc.png");
        when(evidenciaRepo.findByUnidadeTarefaIdIn(List.of(1))).thenReturn(List.of(ev));
        ExecucaoCaderno exec = new ExecucaoCaderno();
        exec.setId(50);
        when(execRepo.findByTarefaId(10)).thenReturn(List.of(exec));

        service.delete(10, 1);

        verify(armazenamento).remover("/arquivos/abc.png");
        verify(evidenciaRepo).deleteAll(List.of(ev));
        verify(checklistRepo).deleteByExecucaoCadernoIdIn(List.of(50));
        verify(execRepo).deleteAll(List.of(exec));
        verify(tuRepo).deleteByTarefaId(10);
        verify(tarefaRepo).delete(t);
    }
}
