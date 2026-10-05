package school.sptech.APIDesbravadores.controller;
import java.util.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.repository.*;
import school.sptech.APIDesbravadores.service.AcessoService;

@RestController
@RequestMapping("/tarefas/opcoes")
@PreAuthorize("@acesso.diretoria()")
public class TarefaOpcoesController {
    private final UnidadeRepository unidades;
    private final CicloRepository ciclos;
    private final CadernoRepository cadernos;
    private final AcessoService acesso;
    public TarefaOpcoesController(UnidadeRepository unidades, CicloRepository ciclos, CadernoRepository cadernos, AcessoService acesso) {
        this.unidades = unidades; this.ciclos = ciclos; this.cadernos = cadernos; this.acesso = acesso;
    }
    public record Opcao(Integer id, String nome) {}
    @GetMapping
    public Map<String, List<Opcao>> listar() {
        var clube = acesso.atual().getIdClube();
        return Map.of("unidades", unidades.findByClubeId(clube).stream().map(u -> new Opcao(u.getId(), u.getNome())).toList(),
                "ciclos", ciclos.findByClubeIdAndAtivoTrue(clube).stream().map(c -> new Opcao(c.getId(), c.getNome())).toList(),
                "cadernos", cadernos.findByClubeId(clube).stream().map(c -> new Opcao(c.getId(), c.getNome())).toList());
    }
}
