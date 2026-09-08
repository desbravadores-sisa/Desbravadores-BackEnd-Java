package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.sptech.APIDesbravadores.domain.Convite;

import java.util.List;

@Repository
public interface ConviteRepository extends JpaRepository<Convite, Integer> {
    List<Convite> findByClubeId(Integer idClube);
}
