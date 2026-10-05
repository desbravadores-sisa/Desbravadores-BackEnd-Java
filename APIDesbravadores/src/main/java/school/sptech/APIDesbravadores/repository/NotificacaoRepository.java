package school.sptech.APIDesbravadores.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import school.sptech.APIDesbravadores.domain.Notificacao;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Integer> {
    Page<Notificacao> findByUsuarioId(Integer usuarioId, Pageable pageable);
    long countByUsuarioIdAndLidaFalse(Integer usuarioId);
    Optional<Notificacao> findByIdAndUsuarioId(Integer id, Integer usuarioId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Notificacao n set n.lida = true where n.usuario.id = :usuarioId and n.lida = false")
    int marcarTodas(Integer usuarioId);
}
