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
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.domain.Usuario;
import school.sptech.APIDesbravadores.dto.UnidadeAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.UnidadeCriacaoDto;
import school.sptech.APIDesbravadores.dto.UnidadeResponseDto;
import school.sptech.APIDesbravadores.dto.UsuarioDetalhesDto;
import school.sptech.APIDesbravadores.service.UnidadeService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UnidadeController.class)
@AutoConfigureMockMvc(addFilters = false)
class UnidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private UnidadeService unidadeService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void listarUnidadesDeveRetornarUnidadesDoClubeDoDiretor() throws Exception {
        autenticar(diretor());
        when(unidadeService.listaUnidade(10)).thenReturn(List.of(unidadeResponse()));

        mockMvc.perform(get("/unidades/diretor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].nome").value("Tigres"));
    }

    @Test
    void cadastrarUnidadeDeveCriarUnidadeNoClubeDoDiretor() throws Exception {
        autenticar(diretor());
        UnidadeCriacaoDto request = new UnidadeCriacaoDto();
        request.setNome("Tigres");
        when(unidadeService.cadastrarUnidade(any(UnidadeCriacaoDto.class), eq(10))).thenReturn(unidadeResponse());

        mockMvc.perform(post("/unidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.nome").value("Tigres"));
    }

    @Test
    void atualizarUnidadeDeveAtualizarUnidade() throws Exception {
        UnidadeAtualizacaoDto request = new UnidadeAtualizacaoDto();
        request.setIdUnidade(2);
        request.setNome("Tigres Atualizado");
        request.setPontuacao(100);

        UnidadeResponseDto response = unidadeResponse();
        response.setNome("Tigres Atualizado");
        response.setPontuacao(100);
        when(unidadeService.atualizarUnidade(any(UnidadeAtualizacaoDto.class))).thenReturn(response);

        mockMvc.perform(put("/unidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Tigres Atualizado"))
                .andExpect(jsonPath("$.pontuacao").value(100));
    }

    @Test
    void deletarUnidadeDeveRemoverUnidade() throws Exception {
        doNothing().when(unidadeService).deletarUnidade(2);

        mockMvc.perform(delete("/unidades/{idUnidade}", 2))
                .andExpect(status().isOk());

        verify(unidadeService).deletarUnidade(2);
    }

    @Test
    void buscarUnidadeConselheiroDeveRetornarUnidadeDoConselheiro() throws Exception {
        autenticar(conselheiro());
        when(unidadeService.buscarUnidadePorId(2)).thenReturn(unidadeResponse());

        mockMvc.perform(get("/unidades/conselheiro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.nome").value("Tigres"));
    }

    private UnidadeResponseDto unidadeResponse() {
        UnidadeResponseDto response = new UnidadeResponseDto();
        response.setId(2);
        response.setNome("Tigres");
        response.setPontuacao(90);
        return response;
    }

    private UsuarioDetalhesDto diretor() {
        return usuarioDetalhes("DIRETOR");
    }

    private UsuarioDetalhesDto conselheiro() {
        return usuarioDetalhes("CONSELHEIRO");
    }

    private UsuarioDetalhesDto usuarioDetalhes(String tipoConta) {
        Clube clube = new Clube();
        clube.setId(10);

        Unidade unidade = new Unidade();
        unidade.setId(2);

        Usuario usuario = new Usuario();
        usuario.setNome("Usuario Teste");
        usuario.setEmail("teste@email.com");
        usuario.setSenha("senha");
        usuario.setTipoConta(tipoConta);
        usuario.setClube(clube);
        usuario.setUnidade(unidade);

        return new UsuarioDetalhesDto(usuario);
    }

    private void autenticar(UsuarioDetalhesDto usuario) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
