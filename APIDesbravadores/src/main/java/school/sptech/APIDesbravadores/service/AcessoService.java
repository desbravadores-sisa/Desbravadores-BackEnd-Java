package school.sptech.APIDesbravadores.service;

import java.text.Normalizer;
import java.util.Set;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;
import school.sptech.APIDesbravadores.dto.UsuarioDetalhesDto;
import school.sptech.APIDesbravadores.exception.AcessoNegadoException;

@Service("acesso")
public class AcessoService {
    public static String normalizar(String perfil) {
        return Normalizer.normalize(perfil, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                .toUpperCase(java.util.Locale.ROOT).replace('-', '_').replace(' ', '_');
    }

    public static boolean perfilDiretoria(String perfil) {
        return perfil != null && Set.of("DIRETORIA", "DIRETOR", "VICE_DIRETOR", "SECRETARIA")
                .contains(normalizar(perfil));
    }

    public UsuarioDetalhesDto atual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UsuarioDetalhesDto usuario)) {
            throw new AcessoNegadoException("Sessão inválida.");
        }
        return usuario;
    }

    public boolean diretoria() { return perfilDiretoria(atual().getPerfil()); }

    public void exigirDiretoria() {
        if (!diretoria()) throw new AcessoNegadoException("Ação restrita à Diretoria.");
    }

    public void exigirConselheiro() {
        if (!"CONSELHEIRO".equalsIgnoreCase(atual().getPerfil()) || atual().getIdUnidade() == null) {
            throw new AcessoNegadoException("Ação restrita ao Conselheiro de uma unidade.");
        }
    }

    public void validarClube(Integer clube) {
        if (!atual().getIdClube().equals(clube)) throw new AcessoNegadoException("Acesso a outro clube negado.");
    }

    public void validarVinculo(TarefaUnidade vinculo) {
        validarClube(vinculo.getTarefa().getClube().getId());
        if (!diretoria() && !vinculo.getUnidade().getId().equals(atual().getIdUnidade())) {
            throw new AcessoNegadoException("Acesso a outra unidade negado.");
        }
    }
}
