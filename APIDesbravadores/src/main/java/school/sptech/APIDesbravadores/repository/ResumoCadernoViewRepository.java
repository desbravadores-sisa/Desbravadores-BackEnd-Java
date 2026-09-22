package school.sptech.APIDesbravadores.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sptech.APIDesbravadores.domain.ResumoCadernoView;

import java.util.List;

@Repository
public interface ResumoCadernoViewRepository extends JpaRepository<ResumoCadernoView, Integer> {

    List<ResumoCadernoView> findByIdClubeOrderByIdadeAlvoAsc(Integer idClube);
}
