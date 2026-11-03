package school.sptech.APIDesbravadores.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.util.HtmlUtils;

@Service
public class EvidenciaEmailService {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(EvidenciaEmailService.class);
    private final JavaMailSender sender;
    private final String from;
    private final String frontend;
    private final boolean enabled;

    public EvidenciaEmailService(JavaMailSender sender, @Value("${app.mail.from:}") String from,
            @Value("${app.frontend-url:http://localhost:5173}") String frontend,
            @Value("${app.mail.enabled:false}") boolean enabled) {
        this.sender=sender; this.from=from; this.frontend=frontend.replaceAll("/+$",""); this.enabled=enabled;
    }

    // Somente envia após o commit: uma entrega desfeita não deve gerar e-mail.
    @TransactionalEventListener
    public void enviar(EvidenciaEnviadaEvent event) {
        if (!enabled || from.isBlank()) {
            LOG.warn("E-mail de análise desabilitado para entrega {}", event.entrega());
            return;
        }
        String link=frontend+"/evidencias";
        String text="Tigre da Montanha\nEvidência aguardando análise\n\nA unidade "+event.unidade()
                +" enviou evidências para a tarefa “"+event.tarefa()+"”.\n"
                +"Entre na aplicação para analisar e aprovar ou solicitar correção:\n"+link
                +"\n\nOs arquivos estão disponíveis somente na aplicação, após o login.";
        String html="""
            <!doctype html><html lang="pt-BR"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"></head>
            <body style="margin:0;background:#f8fafc;font-family:Arial,Helvetica,sans-serif;color:#172033">
            <table role="presentation" width="100%%" cellpadding="0" cellspacing="0"><tr><td align="center" style="padding:32px 16px">
            <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="max-width:600px;background:white;border:1px solid #e2e8f0;border-radius:16px">
            <tr><td style="padding:28px;border-bottom:1px solid #e2e8f0"><strong style="color:#ec5800;font-size:22px">Tigre da Montanha</strong></td></tr>
            <tr><td style="padding:28px"><p style="color:#9a3412;font-size:12px">ANÁLISE DA DIRETORIA</p>
            <h1 style="font-size:26px">Evidência aguardando análise</h1>
            <p style="color:#475569;line-height:1.7">Uma entrega foi enviada para avaliação. Entre na plataforma para aprovar ou solicitar correção.</p>
            <table role="presentation" width="100%%" style="background:#f8fafc;border-radius:12px"><tr><td style="padding:18px;line-height:1.8">
            <strong>Tarefa:</strong> %s<br><strong>Unidade:</strong> %s</td></tr></table>
            <p style="margin:28px 0"><a href="%s" style="display:inline-block;background:#ec5800;color:white;text-decoration:none;border-radius:10px;padding:15px 24px;font-weight:bold">Analisar evidências</a></p>
            <p style="font-size:12px;color:#64748b;line-height:1.6">Os arquivos ficam protegidos na aplicação e exigem login. Este aviso não representa aprovação da entrega.</p>
            <p style="font-size:12px;overflow-wrap:anywhere">Se o botão não funcionar: <a href="%s">%s</a></p>
            </td></tr></table></td></tr></table></body></html>
            """.formatted(escape(event.tarefa()),escape(event.unidade()),escape(link),escape(link),escape(link));
        for (String email:event.destinatarios()) {
            try {
                var message=sender.createMimeMessage();
                var helper=new MimeMessageHelper(message,MimeMessageHelper.MULTIPART_MODE_MIXED,"UTF-8");
                helper.setFrom(from,"Tigre da Montanha"); helper.setTo(email);
                helper.setSubject("Evidência aguardando análise — Tigre da Montanha");
                helper.setText(text,html); sender.send(message);
            } catch (Exception ex) {
                // O sino continua disponível; falha SMTP não desfaz a evidência confirmada.
                LOG.warn("Falha no e-mail de análise da entrega {} ({})",event.entrega(),ex.getClass().getSimpleName());
            }
        }
    }
    private String escape(String value) { return HtmlUtils.htmlEscape(value,"UTF-8"); }
}
