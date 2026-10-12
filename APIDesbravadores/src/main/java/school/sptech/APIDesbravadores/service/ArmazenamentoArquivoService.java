package school.sptech.APIDesbravadores.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.exception.RequisicaoInvalidaException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Guarda arquivos enviados (evidências e fotos de perfil) numa pasta local da API
 * ({@code app.uploads.dir}, variável APP_UPLOADS_DIR). Em produção essa pasta é um volume
 * Docker do container do back; o banco guarda só o caminho público "/arquivos/{nome}".
 */
@Service
public class ArmazenamentoArquivoService {

    public static final String PREFIXO_URL = "/arquivos/";

    private static final Map<String, String> EXTENSOES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp",
            "application/pdf", "pdf");
    private static final Pattern NOME_VALIDO = Pattern.compile("^[a-f0-9-]{36}\\.(jpg|png|webp|pdf)$");

    private final Path pasta;

    public ArmazenamentoArquivoService(@Value("${app.uploads.dir:uploads}") String pasta) {
        this.pasta = Path.of(pasta).toAbsolutePath().normalize();
    }

    /** Salva o arquivo e devolve a URL relativa ("/arquivos/uuid.ext"). */
    public String salvar(MultipartFile arquivo, boolean somenteImagem) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new RequisicaoInvalidaException("Arquivo não enviado");
        }
        String tipo = arquivo.getContentType() == null ? "" : arquivo.getContentType().toLowerCase(Locale.ROOT);
        String extensao = EXTENSOES.get(tipo);
        if (extensao == null || (somenteImagem && "pdf".equals(extensao))) {
            throw new RequisicaoInvalidaException(somenteImagem
                    ? "Envie uma imagem JPG, PNG ou WEBP"
                    : "Envie uma imagem (JPG, PNG, WEBP) ou PDF");
        }
        if (!conteudoConfere(arquivo, extensao)) {
            throw new RequisicaoInvalidaException("O conteúdo do arquivo não corresponde ao tipo informado");
        }
        String nome = UUID.randomUUID() + "." + extensao;
        try (InputStream in = arquivo.getInputStream()) {
            Files.createDirectories(pasta);
            Files.copy(in, pasta.resolve(nome), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível gravar o arquivo", e);
        }
        return PREFIXO_URL + nome;
    }

    /** Confere a assinatura (magic bytes): o Content-Type vem do cliente e não é confiável. */
    static boolean conteudoConfere(MultipartFile arquivo, String extensao) {
        byte[] h = new byte[12];
        int lidos;
        try (InputStream in = arquivo.getInputStream()) {
            lidos = in.readNBytes(h, 0, h.length);
        } catch (IOException e) {
            return false;
        }
        return switch (extensao) {
            case "jpg" -> lidos >= 3 && (h[0] & 0xFF) == 0xFF && (h[1] & 0xFF) == 0xD8 && (h[2] & 0xFF) == 0xFF;
            case "png" -> lidos >= 8 && (h[0] & 0xFF) == 0x89 && h[1] == 'P' && h[2] == 'N' && h[3] == 'G';
            case "webp" -> lidos >= 12 && h[0] == 'R' && h[1] == 'I' && h[2] == 'F' && h[3] == 'F'
                    && h[8] == 'W' && h[9] == 'E' && h[10] == 'B' && h[11] == 'P';
            case "pdf" -> lidos >= 4 && h[0] == '%' && h[1] == 'P' && h[2] == 'D' && h[3] == 'F';
            default -> false;
        };
    }

    public Resource carregar(String nome) {
        if (nome == null || !NOME_VALIDO.matcher(nome).matches()) {
            throw new EntidadeNaoEncontradaException("Arquivo não encontrado");
        }
        Path caminho = pasta.resolve(nome).normalize();
        if (!caminho.startsWith(pasta) || !Files.isRegularFile(caminho)) {
            throw new EntidadeNaoEncontradaException("Arquivo não encontrado");
        }
        try {
            return new UrlResource(caminho.toUri());
        } catch (MalformedURLException e) {
            throw new EntidadeNaoEncontradaException("Arquivo não encontrado");
        }
    }

    /** Remove o arquivo de uma URL gerada por {@link #salvar}; ignora URLs externas. */
    public void remover(String url) {
        if (url == null || !url.startsWith(PREFIXO_URL)) {
            return;
        }
        String nome = url.substring(PREFIXO_URL.length());
        if (!NOME_VALIDO.matcher(nome).matches()) {
            return;
        }
        try {
            Files.deleteIfExists(pasta.resolve(nome));
        } catch (IOException ignored) {
            // arquivo órfão não impede a operação
        }
    }

    public static String tipoMime(String nome) {
        if (nome.endsWith(".png")) return "image/png";
        if (nome.endsWith(".webp")) return "image/webp";
        if (nome.endsWith(".pdf")) return "application/pdf";
        return "image/jpeg";
    }
}
