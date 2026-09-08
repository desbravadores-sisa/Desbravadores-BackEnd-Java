package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sptech.APIDesbravadores.domain.Perfil;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Integer> {
}
