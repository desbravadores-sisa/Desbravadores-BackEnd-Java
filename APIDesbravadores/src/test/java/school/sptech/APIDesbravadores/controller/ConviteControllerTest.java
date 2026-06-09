package school.sptech.APIDesbravadores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.Convite;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.domain.Usuario;
import school.sptech.APIDesbravadores.dto.ConviteRequestDto;
import school.sptech.APIDesbravadores.dto.ConviteResponseDto;
import school.sptech.APIDesbravadores.dto.ConviteUpdateDto;
import school.sptech.APIDesbravadores.dto.UsuarioDetalhesDto;
import school.sptech.APIDesbravadores.service.ConviteService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConviteController.class)
@AutoConfigureMockMvc(addFilters = false)
class ConviteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConviteController conviteController;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private ConviteService conviteService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void listarUnidadesDeveListarConvitesDoClubeDoDiretor() throws Exception {
        autenticar(diretor());
        when(conviteService.listarConvites(1)).thenReturn(List.of(convite()));

        mockMvc.perform(get("/convites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("conselheiro@email.com"))
                .andExpect(jsonPath("$[0].nomeUnidade").value("Tigres"));

        verify(conviteService).listarConvites(1);
    }

    @Test
    void criarConviteDeveCriarConviteParaDiretor() throws Exception {
        autenticar(diretor());
        ConviteRequestDto request = new ConviteRequestDto();
        request.setEmail("conselheiro@email.com");
        request.setTipoConta("CONSELHEIRO");
        request.setDataExpiracao(LocalDate.now().plusDays(7));
        request.setIdUnidade(2);
        request.setIdClube(1);

        when(conviteService.criarConvite(any(ConviteRequestDto.class))).thenReturn(convite());

        mockMvc.perform(post("/convites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipoConta").value("CONSELHEIRO"))
                .andExpect(jsonPath("$.statusConvite").value("pendente"));
    }

    @Test
    void validarConviteDeveRetornarStatusDoConvite() throws Exception {
        when(conviteService.validarConvite(1)).thenReturn(true);

        mockMvc.perform(get("/convites/validar").param("idConvite", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(conviteService).validarConvite(1);
    }

    @Test
    void atualizarConviteDeveAtualizarConvite() throws Exception {
        autenticar(diretor());
        ConviteUpdateDto request = new ConviteUpdateDto();
        request.setStatusConvite("revogado");
        request.setDataExpiracao(LocalDate.now().plusDays(10));

        Convite response = convite();
        response.setStatusConvite("revogado");
        response.setDataExpiracao(LocalDate.now().plusDays(10));
        when(conviteService.atualizarConvite(any(ConviteUpdateDto.class), eq(1))).thenReturn(response);

        ConviteResponseDto body = conviteController.atualizarConvite(request, 1).getBody();

        assertNotNull(body);
        assertEquals(1, body.getId());
        assertEquals("revogado", body.getStatusConvite());
    }

    private Convite convite() {
        Convite convite = new Convite();
        convite.setId(1);
        convite.setEmail("conselheiro@email.com");
        convite.setTipoConta("CONSELHEIRO");
        convite.setDataExpiracao(LocalDate.now().plusDays(7));
        convite.setStatusConvite("pendente");
        convite.setClube(clube());
        convite.setUnidade(unidade());
        return convite;
    }

    private UsuarioDetalhesDto diretor() {
        Usuario usuario = new Usuario();
        usuario.setNome("Maria");
        usuario.setEmail("maria@email.com");
        usuario.setSenha("senha123");
        usuario.setTipoConta("DIRETOR");
        usuario.setClube(clube());
        usuario.setUnidade(null);
        return new UsuarioDetalhesDto(usuario);
    }

    private Clube clube() {
        Clube clube = new Clube();
        clube.setId(1);
        clube.setNome("Clube Central");
        return clube;
    }

    private Unidade unidade() {
        Unidade unidade = new Unidade();
        unidade.setId(2);
        unidade.setNome("Tigres");
        unidade.setClube(clube());
        return unidade;
    }

    private void autenticar(UsuarioDetalhesDto usuario) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
