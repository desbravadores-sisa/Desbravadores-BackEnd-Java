package school.sptech.APIDesbravadores.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.exception.RequisicaoInvalidaException;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArmazenamentoArquivoServiceTest {

    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0};

    @TempDir
    Path pasta;

    @Test
    void salvaComNomeAleatorioEDevolveAUrl() throws Exception {
        ArmazenamentoArquivoService service = new ArmazenamentoArquivoService(pasta.toString());
        String url = service.salvar(new MockMultipartFile("arquivo", "../../x.png", "image/png", PNG), true);
        assertTrue(url.matches("/arquivos/[a-f0-9-]{36}\\.png"), url);
        String nome = url.substring("/arquivos/".length());
        assertEquals(PNG.length, service.carregar(nome).contentLength());
    }

    @Test
    void recusaTipoNaoPermitidoEConteudoDisfarcado() {
        ArmazenamentoArquivoService service = new ArmazenamentoArquivoService(pasta.toString());
        assertThrows(RequisicaoInvalidaException.class, () -> service.salvar(
                new MockMultipartFile("arquivo", "a.html", "text/html", "<script>".getBytes()), false));
        assertThrows(RequisicaoInvalidaException.class, () -> service.salvar(
                new MockMultipartFile("arquivo", "a.png", "image/png", "<html>alert(1)".getBytes()), false));
        assertThrows(RequisicaoInvalidaException.class, () -> service.salvar(
                new MockMultipartFile("arquivo", "a.pdf", "application/pdf", "%PDF-1.4".getBytes()), true));
    }

    @Test
    void naoServeCaminhoForaDaPasta() {
        ArmazenamentoArquivoService service = new ArmazenamentoArquivoService(pasta.toString());
        assertThrows(EntidadeNaoEncontradaException.class, () -> service.carregar("../application.properties"));
        assertThrows(EntidadeNaoEncontradaException.class, () -> service.carregar("00000000-0000-0000-0000-000000000000.png"));
    }
}
