package school.sptech.APIDesbravadores.service;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import school.sptech.APIDesbravadores.domain.EmailPendente;
import school.sptech.APIDesbravadores.repository.EmailPendenteRepository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class EmailServiceTest {
    @SuppressWarnings("unchecked")
    private ObjectProvider<JavaMailSender> provider(JavaMailSender sender) {
        var p = (ObjectProvider<JavaMailSender>) mock(ObjectProvider.class);
        when(p.getIfAvailable()).thenReturn(sender); return p;
    }
    @Test void enviaEmailAposConfirmacaoESinalizaComoEnviado() {
        var repo = mock(EmailPendenteRepository.class); var sender = mock(JavaMailSender.class);
        var email = new EmailPendente(); email.setDestinatario("carlos@tigre.com"); email.setAssunto("Tarefa aprovada - Tigre da Montanha"); email.setConteudo("Campori · Tigresas · 300 pontos");
        when(repo.findTop20ByEnviadoFalseAndTentativasLessThanAndProximaTentativaBeforeOrderByIdAsc(eq(5), any())).thenReturn(List.of(email));
        new EmailService(repo, provider(sender), "http://localhost:5173", "clube@tigre.com", true).enviarPendentes();
        var message = ArgumentCaptor.forClass(SimpleMailMessage.class); verify(sender).send(message.capture());
        assertEquals("carlos@tigre.com", message.getValue().getTo()[0]); assertTrue(message.getValue().getText().contains("300"));
        assertTrue(email.isEnviado()); assertEquals("", email.getConteudo()); verify(repo).save(email);
    }
    @Test void falhaSMTPMantemFilaEProgramaNovaTentativa() {
        var repo = mock(EmailPendenteRepository.class); var sender = mock(JavaMailSender.class); var email = new EmailPendente();
        email.setDestinatario("carlos@tigre.com"); email.setAssunto("Aprovada"); email.setConteudo("300 pontos");
        when(repo.findTop20ByEnviadoFalseAndTentativasLessThanAndProximaTentativaBeforeOrderByIdAsc(eq(5), any())).thenReturn(List.of(email));
        doThrow(new MailSendException("SMTP indisponível")).when(sender).send(any(SimpleMailMessage.class));
        new EmailService(repo, provider(sender), "http://localhost:5173", "clube@tigre.com", true).enviarPendentes();
        assertFalse(email.isEnviado()); assertEquals(1, email.getTentativas());
        assertTrue(email.getProximaTentativa().isAfter(LocalDateTime.now())); assertEquals("300 pontos", email.getConteudo());
    }
    @Test void desenvolvimentoSemSMTPNaoTentaEnviar() {
        var repo = mock(EmailPendenteRepository.class);
        @SuppressWarnings("unchecked") var provider = (ObjectProvider<JavaMailSender>) mock(ObjectProvider.class);
        new EmailService(repo, provider, "http://localhost:5173", "clube@tigre.com", false).enviarPendentes();
        verifyNoInteractions(repo, provider);
    }
}
