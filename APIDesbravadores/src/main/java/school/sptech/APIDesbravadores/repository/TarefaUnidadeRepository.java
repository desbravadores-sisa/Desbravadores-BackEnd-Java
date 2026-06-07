package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sptech.APIDesbravadores.domain.Tarefa;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;

import java.util.Optional;

@Repository
public interface TarefaUnidadeRepository extends JpaRepository<TarefaUnidade, Integer> {
    Optional<TarefaUnidade> findByTarefaId(Integer tarefaId);

    void deleteByTarefaId(Integer tarefaId);
}
