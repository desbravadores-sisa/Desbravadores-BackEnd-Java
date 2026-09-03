package school.sptech.APIDesbravadores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import school.sptech.APIDesbravadores.dto.ClubeAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.ClubeCriacaoDto;
import school.sptech.APIDesbravadores.dto.ClubeResponseDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.service.ClubeService;

import java.time.LocalDateTime;
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

@WebMvcTest(ClubeController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClubeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private ClubeService clubeService;

    @Test
    void createDeveCriarClube() throws Exception {
        ClubeCriacaoDto request = new ClubeCriacaoDto();
        request.setNome("Clube Alfa");
        request.setRegiao("Sul");
        request.setCidade("Sao Paulo");

        when(clubeService.create(any(ClubeCriacaoDto.class))).thenReturn(clubeResponse());

        mockMvc.perform(post("/clubes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Clube Alfa"))
                .andExpect(jsonPath("$.dataCriacao").value("2026-09-03T10:00:00"));
    }

    @Test
    void findAllDeveListarClubes() throws Exception {
        when(clubeService.findAll()).thenReturn(List.of(clubeResponse()));

        mockMvc.perform(get("/clubes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Clube Alfa"));
    }

    @Test
    void findAllDeveRetornarNoContentQuandoNaoExistiremClubes() throws Exception {
        when(clubeService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/clubes"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findByIdDeveBuscarClube() throws Exception {
        when(clubeService.findById(1)).thenReturn(clubeResponse());

        mockMvc.perform(get("/clubes/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateDeveAtualizarClube() throws Exception {
        ClubeAtualizacaoDto request = new ClubeAtualizacaoDto();
        request.setNome("Clube Alfa Atualizado");
        request.setRegiao("Sul");
        request.setCidade("Campinas");

        ClubeResponseDto response = clubeResponse();
        response.setNome("Clube Alfa Atualizado");
        response.setCidade("Campinas");
        when(clubeService.update(eq(1), any(ClubeAtualizacaoDto.class))).thenReturn(response);

        mockMvc.perform(put("/clubes/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Clube Alfa Atualizado"))
                .andExpect(jsonPath("$.cidade").value("Campinas"));
    }

    @Test
    void deleteDeveRemoverClube() throws Exception {
        doNothing().when(clubeService).delete(1);

        mockMvc.perform(delete("/clubes/{id}", 1))
                .andExpect(status().isNoContent());

        verify(clubeService).delete(1);
    }

    @Test
    void findByIdDeveRetornarNotFoundComMotivoQuandoClubeNaoExistir() throws Exception {
        when(clubeService.findById(99))
                .thenThrow(new EntidadeNaoEncontradaException("Clube não encontrado com ID: 99"));

        mockMvc.perform(get("/clubes/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Clube não encontrado com ID: 99"));
    }

    @Test
    void createDeveRetornarBadRequestQuandoPayloadForInvalido() throws Exception {
        mockMvc.perform(post("/clubes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    private ClubeResponseDto clubeResponse() {
        ClubeResponseDto response = new ClubeResponseDto();
        response.setId(1);
        response.setNome("Clube Alfa");
        response.setRegiao("Sul");
        response.setCidade("Sao Paulo");
        response.setDataCriacao(LocalDateTime.of(2026, 9, 3, 10, 0));
        return response;
    }
}
