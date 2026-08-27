package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.APIDesbravadores.domain.Desbravador;

public interface DesbravadorRepository extends JpaRepository<Desbravador, Integer> {
}
