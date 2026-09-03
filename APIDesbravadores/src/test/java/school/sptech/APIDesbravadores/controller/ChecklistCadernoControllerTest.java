package school.sptech.APIDesbravadores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import school.sptech.APIDesbravadores.dto.ChecklistCadernoAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.ChecklistCadernoCriacaoDto;
import school.sptech.APIDesbravadores.dto.ChecklistCadernoResponseDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.service.ChecklistCadernoService;

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

@WebMvcTest(ChecklistCadernoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChecklistCadernoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private ChecklistCadernoService checklistCadernoService;

    @Test
    void createDeveCriarChecklist() throws Exception {
        ChecklistCadernoCriacaoDto request = new ChecklistCadernoCriacaoDto();
        request.setIdExecucaoCaderno(1);
        request.setIdDesbravador(2);
        request.setConcluiuTarefa(true);

        when(checklistCadernoService.create(any(ChecklistCadernoCriacaoDto.class))).thenReturn(checklistResponse());

        mockMvc.perform(post("/checklists-caderno")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.idExecucaoCaderno").value(1))
                .andExpect(jsonPath("$.idDesbravador").value(2))
                .andExpect(jsonPath("$.concluiuTarefa").value(true));
    }

    @Test
    void findAllDeveListarChecklists() throws Exception {
        when(checklistCadernoService.findAll()).thenReturn(List.of(checklistResponse()));

        mockMvc.perform(get("/checklists-caderno"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].concluiuTarefa").value(true));
    }

    @Test
    void findAllDeveRetornarNoContentQuandoNaoExistiremChecklists() throws Exception {
        when(checklistCadernoService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/checklists-caderno"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findByIdDeveBuscarChecklist() throws Exception {
        when(checklistCadernoService.findById(1)).thenReturn(checklistResponse());

        mockMvc.perform(get("/checklists-caderno/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void findByIdDeveRetornarNotFoundComMotivoQuandoChecklistNaoExistir() throws Exception {
        when(checklistCadernoService.findById(99))
                .thenThrow(new EntidadeNaoEncontradaException("Checklist não encontrado com ID: 99"));

        mockMvc.perform(get("/checklists-caderno/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Checklist não encontrado com ID: 99"));
    }

    @Test
    void updateDeveAtualizarChecklist() throws Exception {
        ChecklistCadernoAtualizacaoDto request = new ChecklistCadernoAtualizacaoDto();
        request.setIdExecucaoCaderno(1);
        request.setIdDesbravador(2);
        request.setConcluiuTarefa(false);

        ChecklistCadernoResponseDto response = checklistResponse();
        response.setConcluiuTarefa(false);
        response.setDataMarcacao(null);
        when(checklistCadernoService.update(eq(1), any(ChecklistCadernoAtualizacaoDto.class))).thenReturn(response);

        mockMvc.perform(put("/checklists-caderno/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.concluiuTarefa").value(false))
                .andExpect(jsonPath("$.dataMarcacao").doesNotExist());
    }

    @Test
    void deleteDeveRemoverChecklist() throws Exception {
        doNothing().when(checklistCadernoService).delete(1);

        mockMvc.perform(delete("/checklists-caderno/{id}", 1))
                .andExpect(status().isNoContent());

        verify(checklistCadernoService).delete(1);
    }

    @Test
    void createDeveRetornarBadRequestComCamposQuandoPayloadForInvalido() throws Exception {
        mockMvc.perform(post("/checklists-caderno")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"))
                .andExpect(jsonPath("$.campos.idExecucaoCaderno").exists())
                .andExpect(jsonPath("$.campos.idDesbravador").exists())
                .andExpect(jsonPath("$.campos.concluiuTarefa").exists());
    }

    private ChecklistCadernoResponseDto checklistResponse() {
        ChecklistCadernoResponseDto response = new ChecklistCadernoResponseDto();
        response.setId(1);
        response.setIdExecucaoCaderno(1);
        response.setIdDesbravador(2);
        response.setConcluiuTarefa(true);
        response.setDataMarcacao(LocalDateTime.of(2026, 5, 10, 10, 0));
        return response;
    }
}
