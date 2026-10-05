package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sptech.APIDesbravadores.domain.Evidencia;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvidenciaRepository extends JpaRepository<Evidencia, Integer> {
    boolean existsByTarefaUnidadeId(Integer id);
    Optional<Evidencia> findFirstByTarefaUnidadeIdOrderByIdDesc(Integer id);
    List<Evidencia> findAllByTarefaUnidadeTarefaClubeId(Integer idClube);
    List<Evidencia> findAllByTarefaUnidadeUnidadeId(Integer idUnidade);
    Optional<Evidencia> findByIdAndTarefaUnidadeUnidadeId(Integer id, Integer idUnidade);

}
