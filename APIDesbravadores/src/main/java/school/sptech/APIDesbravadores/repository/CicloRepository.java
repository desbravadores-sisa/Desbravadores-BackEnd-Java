package school.sptech.APIDesbravadores.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.APIDesbravadores.domain.Ciclo;
public interface CicloRepository extends JpaRepository<Ciclo, Integer> {
    List<Ciclo> findByClubeIdAndAtivoTrue(Integer idClube);
}
