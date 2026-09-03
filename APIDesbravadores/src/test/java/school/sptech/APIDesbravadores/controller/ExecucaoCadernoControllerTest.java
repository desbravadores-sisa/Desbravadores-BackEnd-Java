package school.sptech.APIDesbravadores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import school.sptech.APIDesbravadores.dto.ExecucaoCadernoAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.ExecucaoCadernoCriacaoDto;
import school.sptech.APIDesbravadores.dto.ExecucaoCadernoResponseDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.service.ExecucaoCadernoService;

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

@WebMvcTest(ExecucaoCadernoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExecucaoCadernoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private ExecucaoCadernoService execucaoCadernoService;

    @Test
    void createDeveCriarExecucao() throws Exception {
        ExecucaoCadernoCriacaoDto request = new ExecucaoCadernoCriacaoDto();
        request.setIdUnidade(1);
        request.setIdTarefa(2);
        request.setIdCiclo(3);
        request.setStatusKanban("A FAZER");

        when(execucaoCadernoService.create(any(ExecucaoCadernoCriacaoDto.class))).thenReturn(execucaoResponse());

        mockMvc.perform(post("/execucoes-caderno")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.idUnidade").value(1))
                .andExpect(jsonPath("$.idTarefa").value(2))
                .andExpect(jsonPath("$.idCiclo").value(3))
                .andExpect(jsonPath("$.statusKanban").value("A FAZER"));
    }

    @Test
    void findAllDeveListarExecucoes() throws Exception {
        when(execucaoCadernoService.findAll()).thenReturn(List.of(execucaoResponse()));

        mockMvc.perform(get("/execucoes-caderno"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].statusKanban").value("A FAZER"));
    }

    @Test
    void findAllDeveRetornarNoContentQuandoNaoExistiremExecucoes() throws Exception {
        when(execucaoCadernoService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/execucoes-caderno"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findByIdDeveBuscarExecucao() throws Exception {
        when(execucaoCadernoService.findById(1)).thenReturn(execucaoResponse());

        mockMvc.perform(get("/execucoes-caderno/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void findByIdDeveRetornarNotFoundComMotivoQuandoExecucaoNaoExistir() throws Exception {
        when(execucaoCadernoService.findById(99))
                .thenThrow(new EntidadeNaoEncontradaException("Execução de caderno não encontrada com ID: 99"));

        mockMvc.perform(get("/execucoes-caderno/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Execução de caderno não encontrada com ID: 99"));
    }

    @Test
    void updateDeveAtualizarExecucao() throws Exception {
        ExecucaoCadernoAtualizacaoDto request = new ExecucaoCadernoAtualizacaoDto();
        request.setIdUnidade(1);
        request.setIdTarefa(2);
        request.setIdCiclo(3);
        request.setStatusKanban("CONCLUIDA");

        ExecucaoCadernoResponseDto response = execucaoResponse();
        response.setStatusKanban("CONCLUIDA");
        response.setDataConclusao(LocalDateTime.of(2026, 5, 11, 10, 0));
        when(execucaoCadernoService.update(eq(1), any(ExecucaoCadernoAtualizacaoDto.class))).thenReturn(response);

        mockMvc.perform(put("/execucoes-caderno/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusKanban").value("CONCLUIDA"))
                .andExpect(jsonPath("$.dataConclusao").exists());
    }

    @Test
    void deleteDeveRemoverExecucao() throws Exception {
        doNothing().when(execucaoCadernoService).delete(1);

        mockMvc.perform(delete("/execucoes-caderno/{id}", 1))
                .andExpect(status().isNoContent());

        verify(execucaoCadernoService).delete(1);
    }

    @Test
    void createDeveRetornarBadRequestComCamposQuandoPayloadForInvalido() throws Exception {
        mockMvc.perform(post("/execucoes-caderno")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"))
                .andExpect(jsonPath("$.campos.idUnidade").exists())
                .andExpect(jsonPath("$.campos.idTarefa").exists())
                .andExpect(jsonPath("$.campos.idCiclo").exists())
                .andExpect(jsonPath("$.campos.statusKanban").exists());
    }

    private ExecucaoCadernoResponseDto execucaoResponse() {
        ExecucaoCadernoResponseDto response = new ExecucaoCadernoResponseDto();
        response.setId(1);
        response.setIdUnidade(1);
        response.setIdTarefa(2);
        response.setIdCiclo(3);
        response.setStatusKanban("A FAZER");
        return response;
    }
}
