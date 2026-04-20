package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.APIDesbravadores.domain.Clube;

public interface ClubeRepository extends JpaRepository<Clube,Integer> {
}
