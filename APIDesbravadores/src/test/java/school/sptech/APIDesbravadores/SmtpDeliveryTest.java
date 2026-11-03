package school.sptech.APIDesbravadores;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import school.sptech.APIDesbravadores.service.ConviteEmailService;
import school.sptech.APIDesbravadores.domain.*;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

class SmtpDeliveryTest {
    @Test void entregaMensagemAoServidorSmtpLocal() throws Exception {
        try (ServerSocket server=new ServerSocket(0,1,InetAddress.getLoopbackAddress());
             var executor=Executors.newSingleThreadExecutor()) {
            server.setSoTimeout(10000);
            var received=executor.submit(() -> {
                try(Socket client=server.accept()) {
                    client.setSoTimeout(10000);
                    var in=new BufferedReader(new InputStreamReader(client.getInputStream(),StandardCharsets.UTF_8));
                    var out=new PrintWriter(client.getOutputStream(),true,StandardCharsets.UTF_8);
                    out.print("220 localhost test\r\n");out.flush();
                    boolean data=false;
                    StringBuilder message=new StringBuilder();
                    String line;
                    while((line=in.readLine())!=null) {
                        if(data) {
                            if(line.equals(".")) { data=false;out.print("250 accepted\r\n");out.flush(); }
                            else message.append(line).append("\n");
                        } else if(line.startsWith("DATA")) {data=true;out.print("354 send data\r\n");out.flush();}
                        else if(line.startsWith("QUIT")) {out.print("221 bye\r\n");out.flush();break;}
                        else {out.print("250 OK\r\n");out.flush();}
                    }
                    return message.toString();
                }
            });
            var sender=new JavaMailSenderImpl(); sender.setHost("127.0.0.1"); sender.setPort(server.getLocalPort());
            var service=new ConviteEmailService(sender,"convites@example.test","http://localhost:5173",true);
            Perfil perfil=new Perfil();perfil.setNome("DIRETORIA");
            Convite c=new Convite();c.setPerfil(perfil);c.setEmail("destinatario@example.test");c.setToken("token-local-de-teste");
            service.enviar(c);
            String message=received.get(10,TimeUnit.SECONDS);
            assertTrue(message.contains("destinatario@example.test"));
            assertTrue(message.contains("token-local-de-teste"));
        }
    }
}
