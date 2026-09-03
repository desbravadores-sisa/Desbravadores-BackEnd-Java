package school.sptech.APIDesbravadores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import school.sptech.APIDesbravadores.dto.DesbravadorAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.DesbravadorCriacaoDto;
import school.sptech.APIDesbravadores.dto.DesbravadorResponseDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.service.DesbravadorService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DesbravadorController.class)
@AutoConfigureMockMvc(addFilters = false)
class DesbravadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private DesbravadorService desbravadorService;

    @Test
    void createDeveCriarDesbravador() throws Exception {
        DesbravadorCriacaoDto request = new DesbravadorCriacaoDto();
        request.setNome("Lucas Silva");
        request.setIdClube(1);
        request.setIdUnidade(2);
        request.setDataNascimento(LocalDate.of(2012, 5, 10));
        request.setGenero("MASCULINO");
        request.setAtivo(true);

        when(desbravadorService.create(any(DesbravadorCriacaoDto.class))).thenReturn(desbravadorResponse());

        mockMvc.perform(post("/desbravadores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Lucas Silva"))
                .andExpect(jsonPath("$.idClube").value(1))
                .andExpect(jsonPath("$.idUnidade").value(2));
    }

    @Test
    void findAllDeveListarDesbravadores() throws Exception {
        when(desbravadorService.findAll(isNull())).thenReturn(List.of(desbravadorResponse()));

        mockMvc.perform(get("/desbravadores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Lucas Silva"));
    }

    @Test
    void findAllDeveListarDesbravadoresFiltrandoPorClube() throws Exception {
        when(desbravadorService.findAll(1)).thenReturn(List.of(desbravadorResponse()));

        mockMvc.perform(get("/desbravadores").param("idClube", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idClube").value(1));
    }

    @Test
    void findAllDeveRetornarNoContentQuandoNaoExistiremDesbravadores() throws Exception {
        when(desbravadorService.findAll(isNull())).thenReturn(List.of());

        mockMvc.perform(get("/desbravadores"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findByIdDeveBuscarDesbravador() throws Exception {
        when(desbravadorService.findById(1)).thenReturn(desbravadorResponse());

        mockMvc.perform(get("/desbravadores/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateDeveAtualizarDesbravador() throws Exception {
        DesbravadorAtualizacaoDto request = new DesbravadorAtualizacaoDto();
        request.setNome("Lucas Silva Atualizado");
        request.setIdClube(1);
        request.setIdUnidade(2);
        request.setDataNascimento(LocalDate.of(2012, 5, 10));
        request.setGenero("MASCULINO");
        request.setAtivo(true);

        DesbravadorResponseDto response = desbravadorResponse();
        response.setNome("Lucas Silva Atualizado");
        when(desbravadorService.update(eq(1), any(DesbravadorAtualizacaoDto.class))).thenReturn(response);

        mockMvc.perform(put("/desbravadores/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Lucas Silva Atualizado"));
    }

    @Test
    void deleteDeveRemoverDesbravador() throws Exception {
        doNothing().when(desbravadorService).delete(1);

        mockMvc.perform(delete("/desbravadores/{id}", 1))
                .andExpect(status().isNoContent());

        verify(desbravadorService).delete(1);
    }

    @Test
    void findByIdDeveRetornarNotFoundComMotivoQuandoDesbravadorNaoExistir() throws Exception {
        when(desbravadorService.findById(99))
                .thenThrow(new EntidadeNaoEncontradaException("Desbravador não encontrado com ID: 99"));

        mockMvc.perform(get("/desbravadores/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Desbravador não encontrado com ID: 99"));
    }

    @Test
    void createDeveRetornarBadRequestComCamposQuandoPayloadForInvalido() throws Exception {
        mockMvc.perform(post("/desbravadores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"))
                .andExpect(jsonPath("$.campos.nome").exists())
                .andExpect(jsonPath("$.campos.idClube").exists())
                .andExpect(jsonPath("$.campos.idUnidade").exists());
    }

    private DesbravadorResponseDto desbravadorResponse() {
        DesbravadorResponseDto response = new DesbravadorResponseDto();
        response.setId(1);
        response.setIdClube(1);
        response.setIdUnidade(2);
        response.setNome("Lucas Silva");
        response.setDataNascimento(LocalDate.of(2012, 5, 10));
        response.setGenero("MASCULINO");
        response.setAtivo(true);
        return response;
    }
}
