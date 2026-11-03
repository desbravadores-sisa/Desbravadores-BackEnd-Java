package school.sptech.APIDesbravadores;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import school.sptech.APIDesbravadores.domain.*;
import school.sptech.APIDesbravadores.service.ConviteEmailTemplate;
import java.nio.file.*;
import java.time.LocalDateTime;

class ConviteEmailTemplateTest {
    Convite convite() {
        Perfil p = new Perfil(); p.setNome("CONSELHEIRO");
        Unidade u = new Unidade(); u.setNome("Tigresas");
        Convite c = new Convite(); c.setPerfil(p); c.setUnidade(u);
        c.setEmail("conselheiro@example.test"); c.setToken("exemplo-sem-validade");
        c.setDataExpiracao(LocalDateTime.of(2026,11,2,18,0));
        return c;
    }
    @Test void geraPreviaComDadosFicticios() throws Exception {
        var result = ConviteEmailTemplate.renderizar(convite(), "https://exemplo.invalid");
        assertTrue(result.html().contains("Criar minha conta"));
        assertTrue(result.html().contains("02/11/2026 às 18:00"));
        assertTrue(result.texto().contains("Conselheiro"));
        assertFalse(result.html().contains("{{"));
        Files.createDirectories(Path.of("target/email-preview"));
        Files.writeString(Path.of("target/email-preview/convite.html"), result.html());
    }
    @Test void escapaConteudoDinamicoSemInterpretarMarcadores() throws Exception {
        Convite c = convite(); c.getUnidade().setNome("<img src=x onerror=alert(1)> {{link}}");
        var result = ConviteEmailTemplate.renderizar(c, "https://exemplo.invalid");
        assertFalse(result.html().contains("<img"));
        assertTrue(result.html().contains("&lt;img"));
        assertTrue(result.html().contains("{{link}}"));
    }
}
