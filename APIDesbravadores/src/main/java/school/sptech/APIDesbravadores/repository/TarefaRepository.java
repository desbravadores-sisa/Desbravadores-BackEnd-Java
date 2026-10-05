package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sptech.APIDesbravadores.domain.Tarefa;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Integer> {
    java.util.Optional<Tarefa> findByRequestId(String requestId);
    java.util.List<Tarefa> findByClubeId(Integer clubeId);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select t from Tarefa t where t.id = :id")
    java.util.Optional<Tarefa> buscarBloqueada(Integer id);
}
