package school.sptech.APIDesbravadores.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.APIDesbravadores.domain.Unidade;

import java.util.List;

public interface UnidadeRepository extends JpaRepository<Unidade, Integer> {

   List<Unidade> findByClubeId(Integer idClub);
}
