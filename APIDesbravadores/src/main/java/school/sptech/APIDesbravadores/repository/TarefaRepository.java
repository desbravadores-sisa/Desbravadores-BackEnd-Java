package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sptech.APIDesbravadores.domain.Tarefa;

import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Integer> {

    List<Tarefa> findByClubeId(Integer idClube);

    List<Tarefa> findByCadernoIdAndTipoTarefa(Integer idCaderno, String tipoTarefa);
}
