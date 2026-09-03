package school.sptech.APIDesbravadores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import school.sptech.APIDesbravadores.dto.EvidenciaAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.EvidenciaCriacaoDto;
import school.sptech.APIDesbravadores.dto.EvidenciaResponseDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.service.EvidenciaService;

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

@WebMvcTest(EvidenciaController.class)
@AutoConfigureMockMvc(addFilters = false)
class EvidenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private EvidenciaService evidenciaService;

    @Test
    void createDeveCriarEvidencia() throws Exception {
        EvidenciaCriacaoDto request = new EvidenciaCriacaoDto();
        request.setIdTarefaUnidade(1);
        request.setUrlS3("https://s3.amazonaws.com/evidencia.png");
        request.setComentarioFeedback("Foto da atividade");

        when(evidenciaService.create(any(EvidenciaCriacaoDto.class))).thenReturn(evidenciaResponse());

        mockMvc.perform(post("/evidencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.idTarefaUnidade").value(1))
                .andExpect(jsonPath("$.urlS3").value("https://s3.amazonaws.com/evidencia.png"));
    }

    @Test
    void findAllDeveListarEvidencias() throws Exception {
        when(evidenciaService.findAll()).thenReturn(List.of(evidenciaResponse()));

        mockMvc.perform(get("/evidencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].comentarioFeedback").value("Foto da atividade"));
    }

    @Test
    void findAllDeveRetornarNoContentQuandoNaoExistiremEvidencias() throws Exception {
        when(evidenciaService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/evidencias"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findByIdDeveBuscarEvidencia() throws Exception {
        when(evidenciaService.findById(1)).thenReturn(evidenciaResponse());

        mockMvc.perform(get("/evidencias/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void findByIdDeveRetornarNotFoundComMotivoQuandoEvidenciaNaoExistir() throws Exception {
        when(evidenciaService.findById(99))
                .thenThrow(new EntidadeNaoEncontradaException("Evidência não encontrada com ID: 99"));

        mockMvc.perform(get("/evidencias/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Evidência não encontrada com ID: 99"));
    }

    @Test
    void updateDeveAtualizarEvidencia() throws Exception {
        EvidenciaAtualizacaoDto request = new EvidenciaAtualizacaoDto();
        request.setIdTarefaUnidade(1);
        request.setUrlS3("https://s3.amazonaws.com/evidencia-nova.png");
        request.setComentarioFeedback("Comentario atualizado");

        EvidenciaResponseDto response = evidenciaResponse();
        response.setUrlS3("https://s3.amazonaws.com/evidencia-nova.png");
        response.setComentarioFeedback("Comentario atualizado");
        when(evidenciaService.update(eq(1), any(EvidenciaAtualizacaoDto.class))).thenReturn(response);

        mockMvc.perform(put("/evidencias/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.urlS3").value("https://s3.amazonaws.com/evidencia-nova.png"))
                .andExpect(jsonPath("$.comentarioFeedback").value("Comentario atualizado"));
    }

    @Test
    void deleteDeveRemoverEvidencia() throws Exception {
        doNothing().when(evidenciaService).delete(1);

        mockMvc.perform(delete("/evidencias/{id}", 1))
                .andExpect(status().isNoContent());

        verify(evidenciaService).delete(1);
    }

    @Test
    void createDeveRetornarBadRequestComCamposQuandoPayloadForInvalido() throws Exception {
        mockMvc.perform(post("/evidencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"))
                .andExpect(jsonPath("$.campos.idTarefaUnidade").exists());
    }

    private EvidenciaResponseDto evidenciaResponse() {
        EvidenciaResponseDto response = new EvidenciaResponseDto();
        response.setId(1);
        response.setIdTarefaUnidade(1);
        response.setUrlS3("https://s3.amazonaws.com/evidencia.png");
        response.setComentarioFeedback("Foto da atividade");
        response.setDataEnvio(LocalDateTime.of(2026, 5, 10, 10, 0));
        return response;
    }
}
