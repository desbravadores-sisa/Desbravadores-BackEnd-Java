package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.APIDesbravadores.domain.Desbravador;

import java.util.List;

public interface DesbravadorRepository extends JpaRepository<Desbravador, Integer> {
    List<Desbravador> findByClubeId(Integer idClube);
}
