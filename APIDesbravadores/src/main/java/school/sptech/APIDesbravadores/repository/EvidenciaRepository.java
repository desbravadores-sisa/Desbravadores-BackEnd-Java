package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sptech.APIDesbravadores.domain.Evidencia;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvidenciaRepository extends JpaRepository<Evidencia, Integer> {
    List<Evidencia> findAllByTarefaUnidadeTarefaFkClube(Integer fkClube);

    List<Evidencia> findAllByTarefaUnidadeFkUnidade(Integer fkUnidade);

    Optional<Evidencia> findByIdAndTarefaUnidadeFkUnidade(Integer id, Integer fkUnidade);
}
