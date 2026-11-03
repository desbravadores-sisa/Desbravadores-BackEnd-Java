package school.sptech.APIDesbravadores.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import school.sptech.APIDesbravadores.domain.Convite;

@Service
public class ConviteEmailService {
    private final JavaMailSender sender;
    private final String from;
    private final String frontend;
    private final boolean enabled;

    public ConviteEmailService(JavaMailSender sender,
            @Value("${app.mail.from:}") String from,
            @Value("${app.frontend-url:http://localhost:5173}") String frontend,
            @Value("${app.mail.enabled:false}") boolean enabled) {
        this.sender = sender;
        this.from = from;
        this.frontend = frontend.replaceAll("/+$", "");
        this.enabled = enabled;
    }

    public void enviar(Convite convite) {
        if (!enabled || from.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Configure o serviço de e-mail antes de enviar convites.");
        }
        try {
            var conteudo = ConviteEmailTemplate.renderizar(convite, frontend);
            var message = sender.createMimeMessage();
            var helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED, "UTF-8");
            helper.setFrom(from, "Tigre da Montanha");
            helper.setTo(convite.getEmail());
            helper.setSubject("Você foi convidado para o Tigre da Montanha");
            helper.setText(conteudo.texto(), conteudo.html());
            sender.send(message);
        } catch (org.springframework.mail.MailException | jakarta.mail.MessagingException | java.io.IOException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Não foi possível enviar o convite. Tente novamente mais tarde.");
        }
    }
}
