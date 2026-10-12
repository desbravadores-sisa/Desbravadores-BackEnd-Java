package school.sptech.APIDesbravadores.service;

import school.sptech.APIDesbravadores.domain.DesbravadorCaderno;
import school.sptech.APIDesbravadores.domain.Tarefa;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Regra do protótipo: tarefa de caderno só aparece para uma unidade se ela tiver pelo menos
 * um desbravador ativo VINCULADO ao caderno (Desbravador_Caderno, ainda não concluído).
 * Tarefas de clube aparecem sempre. Usada pelo dashboard da Diretoria e pelo Kanban.
 */
final class RegraVisibilidadeCaderno {

    private RegraVisibilidadeCaderno() {
    }

    static Set<Integer> cadernosEmAndamento(Collection<DesbravadorCaderno> vinculosDaUnidade) {
        return vinculosDaUnidade.stream()
                .filter(v -> v.getDataConclusao() == null && v.getDesbravador() != null
                        && !Boolean.FALSE.equals(v.getDesbravador().getAtivo()) && v.getCaderno() != null)
                .map(v -> v.getCaderno().getId())
                .collect(Collectors.toSet());
    }

    static boolean visivel(Tarefa t, Set<Integer> cadernosDaUnidade) {
        if (!"CADERNO".equalsIgnoreCase(t.getTipoTarefa())) {
            return true;
        }
        return t.getCaderno() != null && cadernosDaUnidade.contains(t.getCaderno().getId());
    }
}
