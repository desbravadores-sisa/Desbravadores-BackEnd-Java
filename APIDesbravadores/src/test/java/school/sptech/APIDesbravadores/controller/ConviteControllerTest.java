package school.sptech.APIDesbravadores.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.sptech.APIDesbravadores.domain.*;
import school.sptech.APIDesbravadores.dto.*;
import school.sptech.APIDesbravadores.mapper.ConviteMapper;
import school.sptech.APIDesbravadores.service.*;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class ConviteControllerTest {
    @Mock ConviteService service;
    @Mock EmailService emails;
    @InjectMocks ConviteController controller;
    private final UsuarioDetalhesDto diretor = new UsuarioDetalhesDto(1, "Maria", "maria@email.com", "senha", "DIRETORIA", 1, null);
    private Convite convite() {
        var perfil = new Perfil(); perfil.setNome("CONSELHEIRO");
        var convite = new Convite(); convite.setId(1); convite.setEmail("conselheiro@email.com");
        convite.setPerfil(perfil); convite.setToken("token"); convite.setDataExpiracao(LocalDateTime.now().plusDays(14));
        convite.setStatusConvite("PENDENTE"); return convite;
    }
    @Test void listarDeveUsarClubeDaSessaoEFiltro() {
        when(service.listarConvites(1, "PENDENTE")).thenReturn(List.of(ConviteMapper.toResponse(convite())));
        var response = controller.listarUnidades(diretor, "PENDENTE");
        assertEquals(200, response.getStatusCode().value());
        assertEquals("conselheiro@email.com", response.getBody().getFirst().getEmail());
    }
    @Test void criarDeveRetornarLinkGerado() {
        var request = new ConviteCriacaoRequestDto(); request.setEmail("conselheiro@email.com"); request.setIdPerfil(2);
        var convite = convite();
        when(service.criarConvite(request, 1)).thenReturn(convite);
        when(emails.linkConvite(convite)).thenReturn("http://localhost:5173/cadastro?token=token");
        var response = controller.criarConvite(diretor, request);
        assertEquals(201, response.getStatusCode().value());
        assertTrue(response.getBody().getLink().contains("token=token"));
        assertEquals("CONSELHEIRO", response.getBody().getTipoConta());
    }
    @Test void excluirDeveUsarClubeDaSessao() {
        assertEquals(204, controller.deletarConvite(diretor, 9).getStatusCode().value());
        verify(service).excluirConvite(1, 9);
    }
}
