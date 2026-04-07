package school.sptech.api_tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sptech.api_tasks.domain.Tarefa;
import school.sptech.api_tasks.domain.TarefaUnidade;

import java.util.Optional;

@Repository
public interface TarefaUnidadeRepository extends JpaRepository<TarefaUnidade, Integer> {
    Optional<TarefaUnidade> findByTarefaId(Integer tarefaId);
}
