package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.APIDesbravadores.domain.Evidencia;

public interface EvidenciaRepository extends JpaRepository<Evidencia, Integer> {
    long countByUnidadeTarefaIdIn(java.util.Collection<Integer> unidadeTarefaIds);
    java.util.List<Evidencia> findByUnidadeTarefaIdIn(java.util.Collection<Integer> unidadeTarefaIds);
}
