package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.APIDesbravadores.domain.DesbravadorCaderno;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DesbravadorCadernoRepository extends JpaRepository<DesbravadorCaderno, Integer> {
    List<DesbravadorCaderno> findByCadernoId(Integer idCaderno);
    List<DesbravadorCaderno> findByDesbravadorId(Integer idDesbravador);
    List<DesbravadorCaderno> findByDesbravadorIdIn(Collection<Integer> idsDesbravador);
    List<DesbravadorCaderno> findByDesbravadorUnidadeId(Integer idUnidade);
    Optional<DesbravadorCaderno> findByDesbravadorIdAndCadernoId(Integer idDesbravador, Integer idCaderno);
    void deleteByDesbravadorId(Integer idDesbravador);
    void deleteByCadernoId(Integer idCaderno);
}
