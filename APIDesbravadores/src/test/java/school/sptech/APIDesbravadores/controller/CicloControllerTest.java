package school.sptech.APIDesbravadores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import school.sptech.APIDesbravadores.dto.CicloAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.CicloCriacaoDto;
import school.sptech.APIDesbravadores.dto.CicloResponseDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.service.CicloService;

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

@WebMvcTest(CicloController.class)
@AutoConfigureMockMvc(addFilters = false)
class CicloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private CicloService cicloService;

    @Test
    void createDeveCriarCiclo() throws Exception {
        CicloCriacaoDto request = new CicloCriacaoDto();
        request.setNome("Ciclo 2026");
        request.setIdClube(1);
        request.setDataInicio(LocalDate.of(2026, 2, 1));
        request.setDataFim(LocalDate.of(2026, 11, 30));
        request.setAtivo(true);

        when(cicloService.create(any(CicloCriacaoDto.class))).thenReturn(cicloResponse());

        mockMvc.perform(post("/ciclos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Ciclo 2026"))
                .andExpect(jsonPath("$.idClube").value(1));
    }

    @Test
    void findAllDeveListarCiclos() throws Exception {
        when(cicloService.findAll(isNull())).thenReturn(List.of(cicloResponse()));

        mockMvc.perform(get("/ciclos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Ciclo 2026"));
    }

    @Test
    void findAllDeveListarCiclosFiltrandoPorClube() throws Exception {
        when(cicloService.findAll(1)).thenReturn(List.of(cicloResponse()));

        mockMvc.perform(get("/ciclos").param("idClube", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idClube").value(1));
    }

    @Test
    void findAllDeveRetornarNoContentQuandoNaoExistiremCiclos() throws Exception {
        when(cicloService.findAll(isNull())).thenReturn(List.of());

        mockMvc.perform(get("/ciclos"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findByIdDeveBuscarCiclo() throws Exception {
        when(cicloService.findById(1)).thenReturn(cicloResponse());

        mockMvc.perform(get("/ciclos/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateDeveAtualizarCiclo() throws Exception {
        CicloAtualizacaoDto request = new CicloAtualizacaoDto();
        request.setNome("Ciclo 2026 Atualizado");
        request.setIdClube(1);
        request.setDataInicio(LocalDate.of(2026, 2, 1));
        request.setDataFim(LocalDate.of(2026, 12, 15));
        request.setAtivo(false);

        CicloResponseDto response = cicloResponse();
        response.setNome("Ciclo 2026 Atualizado");
        response.setAtivo(false);
        when(cicloService.update(eq(1), any(CicloAtualizacaoDto.class))).thenReturn(response);

        mockMvc.perform(put("/ciclos/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ciclo 2026 Atualizado"))
                .andExpect(jsonPath("$.ativo").value(false));
    }

    @Test
    void deleteDeveRemoverCiclo() throws Exception {
        doNothing().when(cicloService).delete(1);

        mockMvc.perform(delete("/ciclos/{id}", 1))
                .andExpect(status().isNoContent());

        verify(cicloService).delete(1);
    }

    @Test
    void findByIdDeveRetornarNotFoundComMotivoQuandoCicloNaoExistir() throws Exception {
        when(cicloService.findById(99))
                .thenThrow(new EntidadeNaoEncontradaException("Ciclo não encontrado com ID: 99"));

        mockMvc.perform(get("/ciclos/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Ciclo não encontrado com ID: 99"));
    }

    @Test
    void createDeveRetornarBadRequestComCamposQuandoPayloadForInvalido() throws Exception {
        mockMvc.perform(post("/ciclos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"))
                .andExpect(jsonPath("$.campos.nome").exists())
                .andExpect(jsonPath("$.campos.idClube").exists());
    }

    private CicloResponseDto cicloResponse() {
        CicloResponseDto response = new CicloResponseDto();
        response.setId(1);
        response.setIdClube(1);
        response.setNome("Ciclo 2026");
        response.setDataInicio(LocalDate.of(2026, 2, 1));
        response.setDataFim(LocalDate.of(2026, 11, 30));
        response.setAtivo(true);
        return response;
    }
}
