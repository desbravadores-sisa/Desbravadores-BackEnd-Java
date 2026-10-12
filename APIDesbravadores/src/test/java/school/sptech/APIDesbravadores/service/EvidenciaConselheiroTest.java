package school.sptech.APIDesbravadores.service;

import org.junit.jupiter.api.Test;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.Evidencia;
import school.sptech.APIDesbravadores.domain.Tarefa;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.dto.EvidenciaResponseDto;
import school.sptech.APIDesbravadores.repository.EvidenciaRepository;
import school.sptech.APIDesbravadores.repository.ResumoUnidadeViewRepository;
import school.sptech.APIDesbravadores.repository.TarefaUnidadeRepository;
import school.sptech.APIDesbravadores.domain.ResumoUnidadeView;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** A tela de Evidências mostra o conselheiro da unidade (antes vinha sempre "não informado"). */
class EvidenciaConselheiroTest {

    private static Evidencia evidencia(int id, Unidade unidade) {
        Tarefa t = new Tarefa();
        t.setId(id);
        t.setTitulo("Tarefa " + id);
        TarefaUnidade tu = new TarefaUnidade();
        tu.setId(100 + id);
        tu.setTarefa(t);
        tu.setUnidade(unidade);
        Evidencia e = new Evidencia();
        e.setId(id);
        e.setUnidadeTarefa(tu);
        return e;
    }

    @Test
    void preencheNomeDoConselheiroPelaViewDaUnidade() {
        Clube clube = new Clube();
        clube.setId(1);
        Unidade panteras = new Unidade();
        panteras.setId(3);
        panteras.setClube(clube);
        Unidade semConselheiro = new Unidade();
        semConselheiro.setId(4);
        semConselheiro.setClube(clube);

        EvidenciaRepository evidenciaRepo = mock(EvidenciaRepository.class);
        when(evidenciaRepo.findAll()).thenReturn(List.of(evidencia(1, panteras), evidencia(2, panteras), evidencia(3, semConselheiro)));
        ResumoUnidadeViewRepository viewRepo = mock(ResumoUnidadeViewRepository.class);
        ResumoUnidadeView view = mock(ResumoUnidadeView.class);
        when(view.getNomeConselheiro()).thenReturn("Conselheiro Kanban E2E");
        when(viewRepo.findByIdUnidade(3)).thenReturn(Optional.of(view));
        when(viewRepo.findByIdUnidade(4)).thenReturn(Optional.empty());

        EvidenciaService service = new EvidenciaService(evidenciaRepo, mock(TarefaUnidadeRepository.class),
                mock(NotificacaoService.class));
        service.setResumoUnidadeViewRepository(viewRepo);

        List<EvidenciaResponseDto> lista = service.findAllByClube(1);

        assertEquals("Conselheiro Kanban E2E", lista.get(0).getNomeConselheiro());
        assertEquals("Conselheiro Kanban E2E", lista.get(1).getNomeConselheiro());
        assertNull(lista.get(2).getNomeConselheiro());
        verify(viewRepo, times(1)).findByIdUnidade(3);
    }
}
