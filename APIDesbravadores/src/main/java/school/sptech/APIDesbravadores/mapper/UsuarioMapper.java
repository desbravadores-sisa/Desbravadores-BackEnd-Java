package school.sptech.APIDesbravadores.mapper;

import school.sptech.APIDesbravadores.domain.Usuario;
import school.sptech.APIDesbravadores.dto.UsuarioCriacaoDto;
import school.sptech.APIDesbravadores.dto.UsuarioResponseDto;

public class UsuarioMapper {

    public static Usuario toEntity(UsuarioCriacaoDto request){
        if (request == null){
            return null;
        }
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setSenha(request.getSenha());
        usuario.setEmail(request.getEmail());
        usuario.setAtivo(true);
        return usuario;
    }

    public static UsuarioResponseDto toResponse(Usuario usuario){
        if (usuario == null) {
            return null;
        }
        UsuarioResponseDto dto = new UsuarioResponseDto();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        if (usuario.getClube() != null) {
            dto.setNomeClube(usuario.getClube().getNome());
        }
        if (usuario.getUnidade() != null) {
            dto.setNomeUnidade(usuario.getUnidade().getNome());
        }
        if (usuario.getPerfil() != null) {
            dto.setNomePerfil(usuario.getPerfil().getNome());
        }
        return dto;
    }
}
