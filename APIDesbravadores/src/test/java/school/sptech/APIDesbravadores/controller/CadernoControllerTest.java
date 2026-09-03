package school.sptech.APIDesbravadores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import school.sptech.APIDesbravadores.dto.CadernoAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.CadernoCriacaoDto;
import school.sptech.APIDesbravadores.dto.CadernoResponseDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.service.CadernoService;

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

@WebMvcTest(CadernoController.class)
@AutoConfigureMockMvc(addFilters = false)
class CadernoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private CadernoService cadernoService;

    @Test
    void createDeveCriarCaderno() throws Exception {
        CadernoCriacaoDto request = new CadernoCriacaoDto();
        request.setNome("Amigo da Natureza");
        request.setIdClube(1);
        request.setIdadeAlvo(12);

        when(cadernoService.create(any(CadernoCriacaoDto.class))).thenReturn(cadernoResponse());

        mockMvc.perform(post("/cadernos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Amigo da Natureza"))
                .andExpect(jsonPath("$.idClube").value(1));
    }

    @Test
    void findAllDeveListarCadernos() throws Exception {
        when(cadernoService.findAll(isNull())).thenReturn(List.of(cadernoResponse()));

        mockMvc.perform(get("/cadernos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Amigo da Natureza"));
    }

    @Test
    void findAllDeveListarCadernosFiltrandoPorClube() throws Exception {
        when(cadernoService.findAll(1)).thenReturn(List.of(cadernoResponse()));

        mockMvc.perform(get("/cadernos").param("idClube", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idClube").value(1));
    }

    @Test
    void findAllDeveRetornarNoContentQuandoNaoExistiremCadernos() throws Exception {
        when(cadernoService.findAll(isNull())).thenReturn(List.of());

        mockMvc.perform(get("/cadernos"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findByIdDeveBuscarCaderno() throws Exception {
        when(cadernoService.findById(1)).thenReturn(cadernoResponse());

        mockMvc.perform(get("/cadernos/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateDeveAtualizarCaderno() throws Exception {
        CadernoAtualizacaoDto request = new CadernoAtualizacaoDto();
        request.setNome("Amigo da Natureza Atualizado");
        request.setIdClube(1);
        request.setIdadeAlvo(13);

        CadernoResponseDto response = cadernoResponse();
        response.setNome("Amigo da Natureza Atualizado");
        response.setIdadeAlvo(13);
        when(cadernoService.update(eq(1), any(CadernoAtualizacaoDto.class))).thenReturn(response);

        mockMvc.perform(put("/cadernos/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Amigo da Natureza Atualizado"))
                .andExpect(jsonPath("$.idadeAlvo").value(13));
    }

    @Test
    void deleteDeveRemoverCaderno() throws Exception {
        doNothing().when(cadernoService).delete(1);

        mockMvc.perform(delete("/cadernos/{id}", 1))
                .andExpect(status().isNoContent());

        verify(cadernoService).delete(1);
    }

    @Test
    void findByIdDeveRetornarNotFoundComMotivoQuandoCadernoNaoExistir() throws Exception {
        when(cadernoService.findById(99))
                .thenThrow(new EntidadeNaoEncontradaException("Caderno não encontrado com ID: 99"));

        mockMvc.perform(get("/cadernos/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Caderno não encontrado com ID: 99"));
    }

    @Test
    void createDeveRetornarBadRequestComCamposQuandoPayloadForInvalido() throws Exception {
        mockMvc.perform(post("/cadernos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados de entrada inválidos"))
                .andExpect(jsonPath("$.campos.nome").exists())
                .andExpect(jsonPath("$.campos.idClube").exists());
    }

    private CadernoResponseDto cadernoResponse() {
        CadernoResponseDto response = new CadernoResponseDto();
        response.setId(1);
        response.setIdClube(1);
        response.setNome("Amigo da Natureza");
        response.setIdadeAlvo(12);
        return response;
    }
}
