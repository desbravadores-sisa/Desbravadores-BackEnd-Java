package school.sptech.APIDesbravadores.mapper;

import school.sptech.APIDesbravadores.domain.Perfil;
import school.sptech.APIDesbravadores.domain.Usuario;
import school.sptech.APIDesbravadores.dto.UsuarioCriacaoDto;

public class UsuarioMapper {

    public static Usuario toEntity(UsuarioCriacaoDto request, Perfil perfil){
        if (request == null){
            return null;
        }
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setSenha(request.getSenha());
        usuario.setEmail(request.getEmail());
        usuario.setPerfil(perfil);
        usuario.setAtivo(true);
        return usuario;
    }
}
