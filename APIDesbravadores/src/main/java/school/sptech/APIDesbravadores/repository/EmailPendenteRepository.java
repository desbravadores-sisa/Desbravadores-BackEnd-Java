package school.sptech.APIDesbravadores.repository;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import school.sptech.APIDesbravadores.domain.EmailPendente;

public interface EmailPendenteRepository extends JpaRepository<EmailPendente, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<EmailPendente> findTop20ByEnviadoFalseAndTentativasLessThanAndProximaTentativaBeforeOrderByIdAsc(
            int tentativas, LocalDateTime agora);
}
