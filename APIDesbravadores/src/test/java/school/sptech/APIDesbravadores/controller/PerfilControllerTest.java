package school.sptech.APIDesbravadores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import school.sptech.APIDesbravadores.dto.PerfilAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.PerfilCriacaoDto;
import school.sptech.APIDesbravadores.dto.PerfilResponseDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.service.PerfilService;

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

@WebMvcTest(PerfilController.class)
@AutoConfigureMockMvc(addFilters = false)
class PerfilControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private PerfilService perfilService;

    @Test
    void createDeveCriarPerfil() throws Exception {
        PerfilCriacaoDto request = new PerfilCriacaoDto();
        request.setNome("DIRETOR");
        request.setDescricao("Diretor do clube");

        when(perfilService.create(any(PerfilCriacaoDto.class))).thenReturn(perfilResponse());

        mockMvc.perform(post("/perfis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("DIRETOR"));
    }

    @Test
    void findAllDeveListarPerfis() throws Exception {
        when(perfilService.findAll()).thenReturn(List.of(perfilResponse()));

        mockMvc.perform(get("/perfis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("DIRETOR"));
    }

    @Test
    void findAllDeveRetornarNoContentQuandoNaoExistiremPerfis() throws Exception {
        when(perfilService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/perfis"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findByIdDeveBuscarPerfil() throws Exception {
        when(perfilService.findById(1)).thenReturn(perfilResponse());

        mockMvc.perform(get("/perfis/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateDeveAtualizarPerfil() throws Exception {
        PerfilAtualizacaoDto request = new PerfilAtualizacaoDto();
        request.setNome("DIRETOR ATUALIZADO");
        request.setDescricao("Nova descricao");

        PerfilResponseDto response = perfilResponse();
        response.setNome("DIRETOR ATUALIZADO");
        when(perfilService.update(eq(1), any(PerfilAtualizacaoDto.class))).thenReturn(response);

        mockMvc.perform(put("/perfis/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("DIRETOR ATUALIZADO"));
    }

    @Test
    void deleteDeveRemoverPerfil() throws Exception {
        doNothing().when(perfilService).delete(1);

        mockMvc.perform(delete("/perfis/{id}", 1))
                .andExpect(status().isNoContent());

        verify(perfilService).delete(1);
    }

    @Test
    void findByIdDeveRetornarNotFoundComMotivoQuandoPerfilNaoExistir() throws Exception {
        when(perfilService.findById(99))
                .thenThrow(new EntidadeNaoEncontradaException("Perfil não encontrado com ID: 99"));

        mockMvc.perform(get("/perfis/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Perfil não encontrado com ID: 99"));
    }

    @Test
    void createDeveRetornarBadRequestQuandoPayloadForInvalido() throws Exception {
        mockMvc.perform(post("/perfis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    private PerfilResponseDto perfilResponse() {
        PerfilResponseDto response = new PerfilResponseDto();
        response.setId(1);
        response.setNome("DIRETOR");
        response.setDescricao("Diretor do clube");
        return response;
    }
}
