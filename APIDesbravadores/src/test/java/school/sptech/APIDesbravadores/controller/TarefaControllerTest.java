package school.sptech.APIDesbravadores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import school.sptech.APIDesbravadores.dto.TarefaCreateDto;
import school.sptech.APIDesbravadores.dto.TarefaResponseDto;
import school.sptech.APIDesbravadores.dto.TarefaUpdateDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.exception.RequisicaoInvalidaException;
import school.sptech.APIDesbravadores.service.TarefaService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TarefaController.class)
@AutoConfigureMockMvc(addFilters = false)
class TarefaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private TarefaService tarefaService;

    @Test
    void createDeveCriarTarefa() throws Exception {
        TarefaCreateDto request = new TarefaCreateDto();
        request.setFkClube(1);
        request.setFkUnidade(2);
        request.setNome("Organizar reuniao");
        request.setDescricao("Preparar pauta");
        request.setPontuacao(10);
        request.setPrazoEntrega(LocalDateTime.of(2026, 5, 10, 18, 0));

        when(tarefaService.create(any(TarefaCreateDto.class))).thenReturn(tarefaResponse());

        mockMvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Organizar reuniao"))
                .andExpect(jsonPath("$.statusKanban").value("A fazer"));
    }

    @Test
    void findAllDeveListarTarefas() throws Exception {
        when(tarefaService.findAll()).thenReturn(List.of(tarefaResponse()));

        mockMvc.perform(get("/tarefas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Organizar reuniao"));
    }

    @Test
    void findAllDeveRetornarNoContentQuandoNaoExistiremTarefas() throws Exception {
        when(tarefaService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/tarefas"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findByIdDeveBuscarTarefa() throws Exception {
        when(tarefaService.findById(1)).thenReturn(tarefaResponse());

        mockMvc.perform(get("/tarefas/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateDeveAtualizarTarefa() throws Exception {
        TarefaUpdateDto request = new TarefaUpdateDto();
        request.setNome("Organizar reuniao atualizada");
        request.setDescricao("Nova pauta");
        request.setPontuacao(15);
        request.setPrazoEntrega(LocalDateTime.of(2026, 5, 11, 18, 0));

        TarefaResponseDto response = tarefaResponse();
        response.setNome("Organizar reuniao atualizada");
        response.setPontuacao(15);
        when(tarefaService.update(eq(1), any(TarefaUpdateDto.class))).thenReturn(response);

        mockMvc.perform(put("/tarefas/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Organizar reuniao atualizada"))
                .andExpect(jsonPath("$.pontuacao").value(15));
    }

    @Test
    void deleteDeveRemoverTarefa() throws Exception {
        doNothing().when(tarefaService).delete(1);

        mockMvc.perform(delete("/tarefas/{id}", 1))
                .andExpect(status().isNoContent());

        verify(tarefaService).delete(1);
    }

    @Test
    void updateStatusDeveAtualizarStatusDaTarefa() throws Exception {
        TarefaResponseDto response = tarefaResponse();
        response.setStatusKanban("Em andamento");
        when(tarefaService.updateStatus(1, "Em andamento")).thenReturn(response);

        mockMvc.perform(patch("/tarefas/{id}/status", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "Em andamento"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusKanban").value("Em andamento"));
    }

    @Test
    void updateStatusDeveRetornarBadRequestQuandoStatusNaoForEnviado() throws Exception {
        mockMvc.perform(patch("/tarefas/{id}/status", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatusDeveRetornarBadRequestComMotivoQuandoStatusForInvalido() throws Exception {
        when(tarefaService.updateStatus(1, "Concluido!"))
                .thenThrow(new RequisicaoInvalidaException("Status inválido: Concluido!"));

        mockMvc.perform(patch("/tarefas/{id}/status", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "Concluido!"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Status inválido: Concluido!"))
                .andExpect(jsonPath("$.path").value("/tarefas/1/status"));
    }

    @Test
    void findByIdDeveRetornarNotFoundComMotivoQuandoTarefaNaoExistir() throws Exception {
        when(tarefaService.findById(99))
                .thenThrow(new EntidadeNaoEncontradaException("Tarefa não encontrada com ID: 99"));

        mockMvc.perform(get("/tarefas/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Tarefa não encontrada com ID: 99"));
    }

    @Test
    void createDeveRetornarBadRequestComCamposQuandoPayloadForInvalido() throws Exception {
        mockMvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"))
                .andExpect(jsonPath("$.campos.nome").exists())
                .andExpect(jsonPath("$.campos.fkClube").exists());
    }

    @Test
    void getKanbanDeveListarTarefasAgrupadasPorStatus() throws Exception {
        when(tarefaService.getKanban()).thenReturn(Map.of("A fazer", List.of(tarefaResponse())));

        mockMvc.perform(get("/tarefas/kanban"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['A fazer'][0].id").value(1));
    }

    @Test
    void getKanbanDeveRetornarNoContentQuandoNaoExistiremTarefas() throws Exception {
        when(tarefaService.getKanban()).thenReturn(Map.of());

        mockMvc.perform(get("/tarefas/kanban"))
                .andExpect(status().isNoContent());
    }

    private TarefaResponseDto tarefaResponse() {
        TarefaResponseDto response = new TarefaResponseDto();
        response.setId(1);
        response.setFkClube(1);
        response.setFkUnidade(2);
        response.setNome("Organizar reuniao");
        response.setDescricao("Preparar pauta");
        response.setPontuacao(10);
        response.setPrazoEntrega(LocalDateTime.of(2026, 5, 10, 18, 0));
        response.setDataCriacao(LocalDateTime.of(2026, 5, 2, 12, 0));
        response.setStatusKanban("A fazer");
        return response;
    }
}
