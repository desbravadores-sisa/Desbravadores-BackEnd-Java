package school.sptech.APIDesbravadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sptech.APIDesbravadores.domain.Tarefa;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;

import java.util.Optional;

@Repository
public interface TarefaUnidadeRepository extends JpaRepository<TarefaUnidade, Integer> {
    java.util.List<TarefaUnidade> findAllByTarefaId(Integer tarefaId);
    java.util.List<TarefaUnidade> findAllByUnidadeIdAndCicloAtivoTrue(Integer unidadeId);
    @org.springframework.data.jpa.repository.Query("select tu.id from TarefaUnidade tu where tu.tarefa.id = :tarefaId and tu.unidade.id = :unidadeId and tu.ciclo.ativo = true")
    java.util.List<Integer> buscarIdsAtivos(Integer tarefaId, Integer unidadeId);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select tu from TarefaUnidade tu where tu.id = :id")
    Optional<TarefaUnidade> buscarBloqueado(Integer id);
    @org.springframework.data.jpa.repository.Query("select coalesce(sum(tu.pontuacaoConcedida), 0) from TarefaUnidade tu where tu.unidade.id = :idUnidade")
    Integer pontuacaoUnidade(Integer idUnidade);

}
