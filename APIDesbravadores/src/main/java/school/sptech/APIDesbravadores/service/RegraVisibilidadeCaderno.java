package school.sptech.APIDesbravadores.service;

import school.sptech.APIDesbravadores.domain.Caderno;
import school.sptech.APIDesbravadores.domain.Desbravador;
import school.sptech.APIDesbravadores.domain.Tarefa;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Regra do protótipo: tarefa de caderno só aparece para uma unidade se ela tiver
 * pelo menos um desbravador ativo com a idade-alvo do caderno. Tarefas de clube
 * aparecem sempre. Usada pelo dashboard da Diretoria e pelo Kanban do Conselheiro.
 */
final class RegraVisibilidadeCaderno {

    private RegraVisibilidadeCaderno() {
    }

    static Set<Integer> idadesDaUnidade(Collection<Desbravador> desbravadores, LocalDate hoje) {
        return desbravadores.stream()
                .filter(d -> !Boolean.FALSE.equals(d.getAtivo()) && d.getDataNascimento() != null)
                .map(d -> Period.between(d.getDataNascimento(), hoje).getYears())
                .collect(Collectors.toSet());
    }

    static boolean visivel(Tarefa t, Map<Integer, Caderno> cadernos, Set<Integer> idades) {
        if (!"CADERNO".equalsIgnoreCase(t.getTipoTarefa())) {
            return true;
        }
        if (t.getCaderno() == null) {
            return false;
        }
        Caderno c = cadernos.getOrDefault(t.getCaderno().getId(), t.getCaderno());
        return c.getIdadeAlvo() != null && idades.contains(c.getIdadeAlvo());
    }
}
