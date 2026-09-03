package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.APIDesbravadores.domain.Ciclo;

import java.util.List;
import java.util.Optional;

public interface CicloRepository extends JpaRepository<Ciclo, Integer> {
    Optional<Ciclo> findByClubeIdAndAtivoTrue(Integer idClube);
    List<Ciclo> findByClubeId(Integer idClube);
}
