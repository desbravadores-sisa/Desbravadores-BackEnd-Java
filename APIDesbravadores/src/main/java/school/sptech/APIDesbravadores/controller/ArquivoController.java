package school.sptech.APIDesbravadores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.APIDesbravadores.service.ArmazenamentoArquivoService;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/arquivos")
@Tag(name = "Arquivos", description = "Arquivos enviados (evidências e fotos de perfil); exige login")
public class ArquivoController {

    private final ArmazenamentoArquivoService armazenamento;

    public ArquivoController(ArmazenamentoArquivoService armazenamento) {
        this.armazenamento = armazenamento;
    }

    @GetMapping("/{nome}")
    @Operation(summary = "Baixar arquivo enviado")
    public ResponseEntity<Resource> baixar(@PathVariable String nome) {
        Resource arquivo = armazenamento.carregar(nome);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(ArmazenamentoArquivoService.tipoMime(nome)))
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePrivate())
                .body(arquivo);
    }
}
