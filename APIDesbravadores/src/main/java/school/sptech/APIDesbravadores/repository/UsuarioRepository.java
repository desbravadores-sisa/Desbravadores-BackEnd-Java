package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sptech.APIDesbravadores.domain.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Integer> {

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByUnidadeId(Integer id);
}
