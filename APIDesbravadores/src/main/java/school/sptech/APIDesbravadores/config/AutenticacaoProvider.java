package school.sptech.APIDesbravadores.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import school.sptech.APIDesbravadores.service.AutenticacaoService;

public class AutenticacaoProvider implements AuthenticationProvider {

    private final AutenticacaoService usuarioAutorizacaoService;
    private final PasswordEncoder passwordEncoder;

    public AutenticacaoProvider(AutenticacaoService usuarioAutorizacaoService, PasswordEncoder passwordEncoder) {
        this.usuarioAutorizacaoService = usuarioAutorizacaoService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();

        UserDetails userDetails = this.usuarioAutorizacaoService.loadUserByUsername(username);

        System.out.println("====== RAIO-X DA AUTENTICAÇÃO ======");
        System.out.println("E-mail procurado: [" + username + "]");
        System.out.println("Senha que veio do Banco: [" + userDetails.getPassword() + "]");
        System.out.println("Tamanho da senha do Banco: " + userDetails.getPassword().length() + " caracteres");
        System.out.println("====================================");

        if (this.passwordEncoder.matches(password, userDetails.getPassword())) {

            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        } else {
            throw new BadCredentialsException("Usuário ou Senha inválidos");
        }
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}