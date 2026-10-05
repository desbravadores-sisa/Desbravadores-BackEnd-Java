package school.sptech.APIDesbravadores.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.sptech.APIDesbravadores.domain.*;
import school.sptech.APIDesbravadores.dto.UsuarioDetalhesDto;
import school.sptech.APIDesbravadores.exception.RequisicaoInvalidaException;
import school.sptech.APIDesbravadores.repository.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvidenciaServiceTest {
    @Mock EvidenciaRepository evidencias;
    @Mock TarefaUnidadeRepository vinculos;
    @Mock UsuarioRepository usuarios;
    @Mock TarefaService tarefas;
    @Mock AcessoService acesso;
    @Mock NotificacaoService notificacoes;
    @Mock EmailService emails;
    @InjectMocks EvidenciaService service;
    private Evidencia evidencia;
    @BeforeEach void preparar() {
        when(acesso.atual()).thenReturn(new UsuarioDetalhesDto(1, "Carlos", "carlos@email.com", "senha", "CONSELHEIRO", 1, 2));
        var tu = new TarefaUnidade(); tu.setId(10); tu.setStatusKanban(StatusKanban.EM_ANDAMENTO);
        evidencia = new Evidencia(); evidencia.setId(1); evidencia.setTarefaUnidade(tu);
        when(evidencias.findById(1)).thenReturn(Optional.of(evidencia));
        when(vinculos.buscarBloqueado(10)).thenReturn(Optional.of(tu));
    }
    @Test void deleteDeveBloquearQuandoConcluida() {
        evidencia.getTarefaUnidade().setStatusKanban(StatusKanban.CONCLUIDO);
        assertThrows(RequisicaoInvalidaException.class, () -> service.delete(1, 2));
        verify(evidencias, never()).delete(any());
    }
    @Test void deleteDeveBloquearDuranteRevisao() {
        evidencia.getTarefaUnidade().setStatusKanban(StatusKanban.EM_REVISAO);
        assertThrows(RequisicaoInvalidaException.class, () -> service.delete(1, 2));
    }
    @Test void deleteDeveRemoverQuandoEmAndamento() {
        service.delete(1, 2); verify(evidencias).delete(evidencia);
    }
}
