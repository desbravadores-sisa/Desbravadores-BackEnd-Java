package school.sptech.APIDesbravadores.mapper;

import school.sptech.APIDesbravadores.domain.Usuario;
import school.sptech.APIDesbravadores.dto.UsuarioCriacaoDto;

public class UsuarioMapper {

    public static Usuario toEntity(UsuarioCriacaoDto request){
        if (request == null){
            return null;
        }
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setSenha(request.getSenha());
        usuario.setEmail(request.getEmail());
        usuario.setTipoConta(request.getTipoConta());
        return usuario;
    }
}
