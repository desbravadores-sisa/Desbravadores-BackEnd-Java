package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sptech.APIDesbravadores.domain.ResumoUnidadeView;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumoUnidadeViewRepository extends JpaRepository<ResumoUnidadeView, Integer> {

    List<ResumoUnidadeView> findByIdClubeOrderByPontuacaoDesc(Integer idClube);

    Optional<ResumoUnidadeView> findByIdUnidade(Integer idUnidade);
}
