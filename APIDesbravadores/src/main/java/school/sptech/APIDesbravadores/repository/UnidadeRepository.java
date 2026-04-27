package school.sptech.APIDesbravadores.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.APIDesbravadores.domain.Unidade;

import java.util.List;
import java.util.Optional;

public interface UnidadeRepository extends JpaRepository<Unidade, Integer> {

   List<Unidade> findByClubeId(Integer idClub);

   Boolean existsByClubeIdAndNome(Integer idClube,String nome);

   Optional<Unidade> findByClubeIdAndNome(Integer idClube,String nome);

}
