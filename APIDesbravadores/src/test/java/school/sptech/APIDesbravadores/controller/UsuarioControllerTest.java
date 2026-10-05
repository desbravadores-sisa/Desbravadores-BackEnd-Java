package school.sptech.APIDesbravadores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import school.sptech.APIDesbravadores.domain.Usuario;
import school.sptech.APIDesbravadores.dto.UsuarioCriacaoDto;
import school.sptech.APIDesbravadores.dto.UsuarioLoginDto;
import school.sptech.APIDesbravadores.dto.UsuarioTokenDto;
import school.sptech.APIDesbravadores.service.UsuarioService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    void criarUsuarioDeveCadastrarUsuario() throws Exception {
        UsuarioCriacaoDto request = new UsuarioCriacaoDto();
        request.setNome("Maria");
        request.setEmail("maria@email.com");
        request.setSenha("senha123");
        request.setToken("convite-token");


        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNome("Maria");
        usuario.setEmail("maria@email.com");
        var perfil = new school.sptech.APIDesbravadores.domain.Perfil(); perfil.setNome("DIRETOR"); usuario.setPerfil(perfil);

        when(usuarioService.cadastrarUsuario(any(UsuarioCriacaoDto.class))).thenReturn(usuario);

        mockMvc.perform(post("/usuarios/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.email").value("maria@email.com"));
    }

    @Test
    void loginDeveAutenticarUsuarioECriarCookie() throws Exception {
        UsuarioLoginDto request = new UsuarioLoginDto();
        request.setEmail("maria@email.com");
        request.setSenha("senha123");

        when(usuarioService.autenticar(any(UsuarioLoginDto.class)))
                .thenReturn(new UsuarioTokenDto(1, "Maria", "maria@email.com", "DIRETOR", "jwt-token"));

        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().value("authToken", "jwt-token"))
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.email").value("maria@email.com"))
                .andExpect(jsonPath("$.tipoConta").value("DIRETOR"));
    }

    @Test
    void logoffDeveInvalidarCookie() throws Exception {
        mockMvc.perform(post("/usuarios/logoff").cookie(new Cookie("authToken", "jwt-token")))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("authToken=")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("Max-Age=0")));
    }

    @Test
    void buscarUsuarioDeveRetornarSessaoSemToken() throws Exception {
        var principal = new school.sptech.APIDesbravadores.dto.UsuarioDetalhesDto(1, "Maria", "maria@email.com", "senha", "DIRETORIA", 1, null);
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        try { mockMvc.perform(get("/usuarios/buscarUsuario"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.token").doesNotExist());
        } finally { org.springframework.security.core.context.SecurityContextHolder.clearContext(); }
    }
}
