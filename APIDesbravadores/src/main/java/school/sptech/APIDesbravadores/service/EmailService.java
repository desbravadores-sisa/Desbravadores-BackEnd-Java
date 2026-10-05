package school.sptech.APIDesbravadores.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import school.sptech.APIDesbravadores.domain.*;
import school.sptech.APIDesbravadores.repository.EmailPendenteRepository;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final EmailPendenteRepository repository;
    private final ObjectProvider<JavaMailSender> sender;
    private final String frontendUrl;
    private final String from;
    private final boolean enabled;

    public EmailService(EmailPendenteRepository repository, ObjectProvider<JavaMailSender> sender,
            @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl,
            @Value("${app.mail.from:noreply@tigredamontanha.local}") String from,
            @Value("${app.mail.enabled:false}") boolean enabled) {
        this.repository = repository; this.sender = sender; this.frontendUrl = frontendUrl.replaceAll("/+$", "");
        this.from = from; this.enabled = enabled;
    }

    public String linkConvite(Convite convite) { return frontendUrl + "/cadastro?token=" + convite.getToken(); }

    @Transactional
    public void sendInvitationEmail(Convite convite) {
        enfileirar(convite.getEmail(), "Convite - Tigre da Montanha",
                "Você foi convidado para o Clube Tigre da Montanha.\nFunção: " + convite.getPerfil().getNome()
                        + (convite.getUnidade() == null ? "" : "\nUnidade: " + convite.getUnidade().getNome())
                        + "\nConclua seu cadastro: " + linkConvite(convite)
                        + "\nVálido até: " + convite.getDataExpiracao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        + " (horário de Brasília).");
    }

    @Transactional
    public void sendTaskApprovedEmail(Usuario usuario, TarefaUnidade tu) {
        enfileirar(usuario.getEmail(), "Tarefa aprovada - Tigre da Montanha",
                "Olá, " + usuario.getNome() + ".\nA evidência foi aprovada pela Diretoria.\nTarefa: "
                        + tu.getTarefa().getNome() + "\nUnidade: " + tu.getUnidade().getNome()
                        + "\nPontuação concedida: " + tu.getPontuacaoConcedida()
                        + "\nAcompanhe: " + frontendUrl + "/minhas-tarefas?tarefa=" + tu.getTarefa().getId());
    }

    private void enfileirar(String para, String assunto, String conteudo) {
        var email = new EmailPendente();
        email.setDestinatario(para); email.setAssunto(assunto); email.setConteudo(conteudo);
        repository.save(email);
    }

    // A fila é gravada na transação de negócio; somente registros confirmados chegam ao SMTP.
    @Scheduled(fixedDelayString = "${app.mail.interval-ms:15000}")
    @Transactional
    public void enviarPendentes() {
        if (!enabled) return;
        var mailSender = sender.getIfAvailable();
        if (mailSender == null) { log.error("MAIL_ENABLED requer configuração de SMTP."); return; }
        for (var email : repository.findTop20ByEnviadoFalseAndTentativasLessThanAndProximaTentativaBeforeOrderByIdAsc(5, LocalDateTime.now())) {
            email.setTentativas(email.getTentativas() + 1);
            try {
                var message = new SimpleMailMessage();
                message.setFrom(from); message.setTo(email.getDestinatario());
                message.setSubject(email.getAssunto()); message.setText(email.getConteudo());
                mailSender.send(message);
                email.setEnviado(true);
                email.setConteudo(""); // Não reter tokens de convite depois do envio.
            } catch (org.springframework.mail.MailException ex) {
                email.setProximaTentativa(LocalDateTime.now().plusMinutes(email.getTentativas() * 2L));
                log.warn("Falha SMTP no e-mail id={}; tentativa={}. Verifique a configuração de envio.",
                        email.getId(), email.getTentativas());
            }
            repository.save(email);
        }
    }
}
