package school.sptech.APIDesbravadores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.domain.Usuario;
import school.sptech.APIDesbravadores.dto.EvidenciaCreateDto;
import school.sptech.APIDesbravadores.dto.EvidenciaResponseDto;
import school.sptech.APIDesbravadores.dto.EvidenciaUpdateDto;
import school.sptech.APIDesbravadores.dto.UsuarioDetalhesDto;
import school.sptech.APIDesbravadores.service.EvidenciaService;

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

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createDeveAnexarEvidencia() throws Exception {
        autenticar(conselheiro());
        EvidenciaCreateDto request = new EvidenciaCreateDto();
        request.setIdTarefa(1);
        request.setNome("Foto da atividade");
        request.setUrlAnexo("https://storage.exemplo.com/evidencias/foto.jpg");

        when(evidenciaService.create(any(EvidenciaCreateDto.class), eq(2))).thenReturn(evidenciaResponse());

        mockMvc.perform(post("/evidencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.idUnidade").value(2))
                .andExpect(jsonPath("$.nome").value("Foto da atividade"));
    }

    @Test
    void createDeveRetornarBadRequestQuandoDadosInvalidos() throws Exception {
        autenticar(conselheiro());

        mockMvc.perform(post("/evidencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllByClubeDeveListarEvidenciasParaDiretor() throws Exception {
        autenticar(diretor());
        when(evidenciaService.findAllByClube(1)).thenReturn(List.of(evidenciaResponse()));

        mockMvc.perform(get("/evidencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].urlAnexo").value("https://storage.exemplo.com/evidencias/foto.jpg"));
    }

    @Test
    void findAllByClubeDeveRetornarNoContentQuandoNaoExistiremEvidencias() throws Exception {
        autenticar(diretor());
        when(evidenciaService.findAllByClube(1)).thenReturn(List.of());

        mockMvc.perform(get("/evidencias"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findAllByUnidadeDeveListarEvidenciasParaConselheiro() throws Exception {
        autenticar(conselheiro());
        when(evidenciaService.findAllByUnidade(2)).thenReturn(List.of(evidenciaResponse()));

        mockMvc.perform(get("/evidencias/unidade"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUnidade").value(2));
    }

    @Test
    void updateDeveEditarEvidencia() throws Exception {
        autenticar(conselheiro());
        EvidenciaUpdateDto request = new EvidenciaUpdateDto();
        request.setNome("Foto atualizada");
        request.setUrlAnexo("https://storage.exemplo.com/evidencias/foto-atualizada.jpg");

        EvidenciaResponseDto response = evidenciaResponse();
        response.setNome("Foto atualizada");
        response.setUrlAnexo("https://storage.exemplo.com/evidencias/foto-atualizada.jpg");
        when(evidenciaService.update(eq(1), any(EvidenciaUpdateDto.class), eq(2))).thenReturn(response);

        mockMvc.perform(put("/evidencias/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Foto atualizada"))
                .andExpect(jsonPath("$.urlAnexo").value("https://storage.exemplo.com/evidencias/foto-atualizada.jpg"));
    }

    @Test
    void deleteDeveRemoverEvidencia() throws Exception {
        autenticar(conselheiro());
        doNothing().when(evidenciaService).delete(1, 2);

        mockMvc.perform(delete("/evidencias/{id}", 1))
                .andExpect(status().isNoContent());

        verify(evidenciaService).delete(1, 2);
    }

    private EvidenciaResponseDto evidenciaResponse() {
        EvidenciaResponseDto response = new EvidenciaResponseDto();
        response.setId(1);
        response.setIdTarefaUnidade(10);
        response.setIdTarefa(1);
        response.setIdUnidade(2);
        response.setNome("Foto da atividade");
        response.setUrlAnexo("https://storage.exemplo.com/evidencias/foto.jpg");
        response.setStatusKanban("Em andamento");
        response.setDataUpload(LocalDateTime.of(2026, 6, 7, 14, 30));
        return response;
    }

    private UsuarioDetalhesDto diretor() {
        Usuario usuario = usuario("DIRETOR");
        usuario.setUnidade(null);
        return new UsuarioDetalhesDto(usuario);
    }

    private UsuarioDetalhesDto conselheiro() {
        return new UsuarioDetalhesDto(usuario("CONSELHEIRO"));
    }

    private Usuario usuario(String tipoConta) {
        Clube clube = new Clube();
        clube.setId(1);

        Unidade unidade = new Unidade();
        unidade.setId(2);
        unidade.setClube(clube);

        Usuario usuario = new Usuario();
        usuario.setNome("Maria");
        usuario.setEmail("maria@email.com");
        usuario.setSenha("senha123");
        usuario.setTipoConta(tipoConta);
        usuario.setClube(clube);
        usuario.setUnidade(unidade);
        return usuario;
    }

    private void autenticar(UsuarioDetalhesDto usuario) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
