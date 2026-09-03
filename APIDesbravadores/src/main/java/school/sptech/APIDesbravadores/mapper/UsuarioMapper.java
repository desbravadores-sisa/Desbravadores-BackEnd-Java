package school.sptech.APIDesbravadores.mapper;

import school.sptech.APIDesbravadores.domain.Perfil;
import school.sptech.APIDesbravadores.domain.Usuario;
import school.sptech.APIDesbravadores.dto.UsuarioCriacaoDto;
import school.sptech.APIDesbravadores.dto.UsuarioResponseDto;

public class UsuarioMapper {

    public static UsuarioResponseDto toResponse(Usuario usuario){
        if (usuario == null){
            return null;
        }
        UsuarioResponseDto dto = new UsuarioResponseDto();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setTipoConta(usuario.getPerfil() != null ? usuario.getPerfil().getNome() : null);
        if (usuario.getClube() != null){
            dto.setIdClube(usuario.getClube().getId());
        }
        if (usuario.getUnidade() != null){
            dto.setIdUnidade(usuario.getUnidade().getId());
        }
        return dto;
    }

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
