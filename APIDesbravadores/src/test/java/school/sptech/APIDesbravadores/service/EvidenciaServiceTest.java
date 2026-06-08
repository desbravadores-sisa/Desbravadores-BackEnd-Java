package school.sptech.APIDesbravadores.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.sptech.APIDesbravadores.domain.Evidencia;
import school.sptech.APIDesbravadores.domain.StatusKanban;
import school.sptech.APIDesbravadores.domain.Tarefa;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;
import school.sptech.APIDesbravadores.exception.RequisicaoInvalidaException;
import school.sptech.APIDesbravadores.repository.EvidenciaRepository;
import school.sptech.APIDesbravadores.repository.TarefaUnidadeRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvidenciaServiceTest {

    @Mock
    private EvidenciaRepository evidenciaRepository;

    @Mock
    private TarefaUnidadeRepository tarefaUnidadeRepository;

    @InjectMocks
    private EvidenciaService evidenciaService;

    @Test
    void deleteDeveBloquearQuandoTarefaEstaConcluida() {
        Evidencia evidencia = evidencia(StatusKanban.CONCLUIDO);
        when(evidenciaRepository.findByIdAndTarefaUnidadeFkUnidade(1, 2)).thenReturn(Optional.of(evidencia));

        assertThrows(RequisicaoInvalidaException.class, () -> evidenciaService.delete(1, 2));

        verify(evidenciaRepository, never()).delete(evidencia);
    }

    @Test
    void deleteDeveRemoverQuandoTarefaNaoEstaConcluida() {
        Evidencia evidencia = evidencia(StatusKanban.EM_ANDAMENTO);
        when(evidenciaRepository.findByIdAndTarefaUnidadeFkUnidade(1, 2)).thenReturn(Optional.of(evidencia));

        evidenciaService.delete(1, 2);

        verify(evidenciaRepository).delete(evidencia);
    }

    private Evidencia evidencia(StatusKanban statusKanban) {
        Tarefa tarefa = new Tarefa();
        tarefa.setId(1);

        TarefaUnidade tarefaUnidade = new TarefaUnidade();
        tarefaUnidade.setId(10);
        tarefaUnidade.setTarefa(tarefa);
        tarefaUnidade.setFkUnidade(2);
        tarefaUnidade.setStatusKanban(statusKanban);

        Evidencia evidencia = new Evidencia();
        evidencia.setId(1);
        evidencia.setTarefaUnidade(tarefaUnidade);
        evidencia.setNome("Foto da atividade");
        evidencia.setUrlAnexo("https://storage.exemplo.com/evidencias/foto.jpg");
        return evidencia;
    }
}
