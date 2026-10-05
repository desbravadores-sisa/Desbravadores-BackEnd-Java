package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sptech.APIDesbravadores.domain.Clube;

@Repository
public interface ClubeRepository extends JpaRepository<Clube,Integer> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select c from Clube c where c.id = :id")
    java.util.Optional<Clube> buscarBloqueado(Integer id);
}
