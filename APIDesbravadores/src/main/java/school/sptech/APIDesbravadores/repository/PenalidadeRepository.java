package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.APIDesbravadores.domain.Penalidade;

import java.util.List;

public interface PenalidadeRepository extends JpaRepository<Penalidade, Integer> {
    List<Penalidade> findByUnidadeIdAndCicloIdOrderByDataRegistroDesc(Integer idUnidade, Integer idCiclo);
    List<Penalidade> findByUnidadeIdOrderByDataRegistroDesc(Integer idUnidade);
    void deleteByUnidadeId(Integer idUnidade);
}
