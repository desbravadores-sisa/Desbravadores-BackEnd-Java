package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.APIDesbravadores.domain.Caderno;

import java.util.List;

public interface CadernoRepository extends JpaRepository<Caderno, Integer> {
    List<Caderno> findByClubeId(Integer idClube);
}
