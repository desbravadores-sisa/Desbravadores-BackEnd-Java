package school.sptech.APIDesbravadores.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import school.sptech.APIDesbravadores.domain.Usuario;
import school.sptech.APIDesbravadores.dto.UsuarioDetalhesDto;
import school.sptech.APIDesbravadores.repository.UsuarioRepository;

import java.util.Optional;

@Service
public class AutenticacaoService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public AutenticacaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(username);

        if (usuarioOpt.isEmpty()){
            throw new UsernameNotFoundException(String.format("Usuário: %s não encontrado", username));
        }
        System.out.println(usuarioOpt.get());
        return new UsuarioDetalhesDto(usuarioOpt.get());
    }
}
