package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.APIDesbravadores.domain.ChecklistCaderno;

public interface ChecklistCadernoRepository extends JpaRepository<ChecklistCaderno, Integer> {
    java.util.List<ChecklistCaderno> findByExecucaoCadernoId(Integer idExecucao);
    java.util.Optional<ChecklistCaderno> findFirstByExecucaoCadernoIdAndDesbravadorId(Integer idExecucao, Integer idDesbravador);
    /** Checklists marcados do desbravador em execuções de tarefas de um caderno (qualquer ciclo). */
    java.util.List<ChecklistCaderno> findByDesbravadorIdAndConcluiuTarefaTrueAndExecucaoCadernoTarefaCadernoId(Integer idDesbravador, Integer idCaderno);
    java.util.List<ChecklistCaderno> findByDesbravadorIdInAndConcluiuTarefaTrue(java.util.Collection<Integer> idsDesbravador);
    java.util.List<ChecklistCaderno> findByExecucaoCadernoUnidadeClubeId(Integer idClube);
    void deleteByDesbravadorId(Integer idDesbravador);
    void deleteByExecucaoCadernoIdIn(java.util.Collection<Integer> idsExecucao);
}
