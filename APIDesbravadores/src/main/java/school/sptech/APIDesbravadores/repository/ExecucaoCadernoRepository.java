package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.APIDesbravadores.domain.ExecucaoCaderno;

public interface ExecucaoCadernoRepository extends JpaRepository<ExecucaoCaderno, Integer> {
    java.util.Optional<ExecucaoCaderno> findFirstByUnidadeIdAndTarefaIdAndCicloIdOrderByIdAsc(Integer idUnidade, Integer idTarefa, Integer idCiclo);
    java.util.List<ExecucaoCaderno> findByUnidadeClubeId(Integer idClube);
}
