package school.sptech.APIDesbravadores.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.util.HtmlUtils;
import school.sptech.APIDesbravadores.domain.Convite;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;

public final class ConviteEmailTemplate {
    private ConviteEmailTemplate() {}

    public record Conteudo(String texto, String html) {}

    public static Conteudo renderizar(Convite convite, String frontend) throws IOException {
        String perfil = "CONSELHEIRO".equalsIgnoreCase(convite.getPerfil().getNome()) ? "Conselheiro" : "Diretoria";
        String unidade = convite.getUnidade() == null ? "Visão geral do clube" : convite.getUnidade().getNome();
        String validade = convite.getDataExpiracao() == null ? "14 dias após a emissão"
                : convite.getDataExpiracao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"));
        String link = frontend.replaceAll("/+$", "") + "/cadastro?token="
                + URLEncoder.encode(convite.getToken(), StandardCharsets.UTF_8);
        String texto = "Tigre da Montanha\nVocê foi convidado!\n\n"
                + "A diretoria convidou você para participar da plataforma do clube.\n"
                + "Perfil: " + perfil + "\nUnidade / acesso: " + unidade
                + "\nE-mail autorizado: " + convite.getEmail() + "\nVálido até: " + validade
                + "\n\nCrie sua conta: " + link
                + "\n\nUse o mesmo e-mail que recebeu este convite. O link pode ser usado uma única vez."
                + "\nSe não esperava este convite, ignore esta mensagem."
                + "\nMensagem automática. Para obter ajuda, fale com a diretoria do clube.";
        String html = new ClassPathResource("templates/email/convite.html").getContentAsString(StandardCharsets.UTF_8);
        // Uma única passagem evita interpretar marcadores dentro de conteúdo fornecido por usuários.
        var valores = java.util.Map.of("perfil", perfil, "unidade", unidade, "email", convite.getEmail(), "validade", validade, "link", link,
                "perfilFundo", "Conselheiro".equals(perfil) ? "#dbeafe" : "#f3e8ff",
                "perfilCor", "Conselheiro".equals(perfil) ? "#1d4ed8" : "#7e22ce");
        var matcher = java.util.regex.Pattern.compile("\\{\\{(perfil|unidade|email|validade|link|perfilFundo|perfilCor)\\}\\}").matcher(html);
        html = matcher.replaceAll(match -> java.util.regex.Matcher.quoteReplacement(HtmlUtils.htmlEscape(valores.get(match.group(1)), "UTF-8")));
        return new Conteudo(texto, html);
    }
}
