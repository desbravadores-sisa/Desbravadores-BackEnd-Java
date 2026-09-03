package school.sptech.APIDesbravadores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import school.sptech.APIDesbravadores.dto.TarefaResponseDto;
import school.sptech.APIDesbravadores.service.TarefaService;

import java.time.LocalDate;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TarefaUnidadeController.class)
@AutoConfigureMockMvc(addFilters = false)
class TarefaUnidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private TarefaService tarefaService;

    @Test
    void findStatusByTarefaIdDeveVisualizarStatusDaTarefa() throws Exception {
        when(tarefaService.findStatusByTarefaId(1)).thenReturn(tarefaResponse());

        mockMvc.perform(get("/tarefas-unidades/{idTarefa}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fkUnidade").value(2))
                .andExpect(jsonPath("$.statusKanban").value("A fazer"));
    }

    @Test
    void updateStatusDeveMoverStatusDaTarefa() throws Exception {
        TarefaResponseDto response = tarefaResponse();
        response.setStatusKanban("Concluído");
        when(tarefaService.updateStatus(1, "Concluído")).thenReturn(response);

        mockMvc.perform(put("/tarefas-unidades/{idTarefa}/status", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "Concluído"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusKanban").value("Concluído"));
    }

    @Test
    void updateStatusDeveRetornarBadRequestQuandoStatusNaoForEnviado() throws Exception {
        mockMvc.perform(put("/tarefas-unidades/{idTarefa}/status", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of())))
                .andExpect(status().isBadRequest());
    }

    private TarefaResponseDto tarefaResponse() {
        TarefaResponseDto response = new TarefaResponseDto();
        response.setId(1);
        response.setFkClube(1);
        response.setFkUnidade(2);
        response.setTitulo("Organizar reuniao");
        response.setDescricao("Preparar pauta");
        response.setPontuacao(10);
        response.setPrazoPadrao(LocalDate.of(2026, 5, 10));
        response.setStatusKanban("A fazer");
        return response;
    }
}
