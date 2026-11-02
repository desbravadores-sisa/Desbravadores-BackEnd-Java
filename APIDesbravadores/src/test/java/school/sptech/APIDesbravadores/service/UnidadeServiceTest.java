package school.sptech.APIDesbravadores.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.sptech.APIDesbravadores.dto.UnidadeAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.UnidadeCriacaoDto;
import school.sptech.APIDesbravadores.exception.RequisicaoInvalidaException;
import school.sptech.APIDesbravadores.repository.ClubeRepository;
import school.sptech.APIDesbravadores.repository.ResumoUnidadeViewRepository;
import school.sptech.APIDesbravadores.repository.UnidadeRepository;
import school.sptech.APIDesbravadores.repository.UsuarioRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class UnidadeServiceTest {

    @Mock
    private UnidadeRepository unidadeRepository;

    @Mock
    private ClubeRepository clubeRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ResumoUnidadeViewRepository resumoUnidadeViewRepository;

    @InjectMocks
    private UnidadeService unidadeService;

    @Test
    void cadastrarUnidadeDeveRejeitarIdadeMaximaIgualOuMaiorQueMinimaAntesDeConsultarRepositorio() {
        UnidadeCriacaoDto request = new UnidadeCriacaoDto();
        request.setIdadeMinima(10);
        request.setIdadeMaxima(10);

        RequisicaoInvalidaException excecao = assertThrows(
                RequisicaoInvalidaException.class,
                () -> unidadeService.cadastrarUnidade(request, 1)
        );

        assertEquals("A idade máxima deve ser menor que a idade mínima necessária.", excecao.getMessage());
        verifyNoInteractions(clubeRepository, unidadeRepository);
    }

    @Test
    void atualizarUnidadeDeveRejeitarIdadeMaximaIgualOuMaiorQueMinimaAntesDeConsultarRepositorio() {
        UnidadeAtualizacaoDto request = new UnidadeAtualizacaoDto();
        request.setIdUnidade(1);
        request.setIdadeMinima(10);
        request.setIdadeMaxima(11);

        RequisicaoInvalidaException excecao = assertThrows(
                RequisicaoInvalidaException.class,
                () -> unidadeService.atualizarUnidade(request, 1)
        );

        assertEquals("A idade máxima deve ser menor que a idade mínima necessária.", excecao.getMessage());
        verifyNoInteractions(clubeRepository, unidadeRepository);
    }
}