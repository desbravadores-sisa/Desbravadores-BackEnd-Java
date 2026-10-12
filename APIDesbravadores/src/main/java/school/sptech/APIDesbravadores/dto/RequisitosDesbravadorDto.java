package school.sptech.APIDesbravadores.dto;

import java.time.LocalDateTime;
import java.util.List;

/** Requisitos individuais de um desbravador, por caderno vinculado. */
public record RequisitosDesbravadorDto(Integer idDesbravador, String nome, List<Caderno> cadernos) {

    public record Caderno(Integer idCaderno, String nome, LocalDateTime dataConclusao, List<Requisito> requisitos) {}

    /** checkinDisponivel: a tarefa está no quadro da unidade (ciclo ativo) e ainda não foi concluída. */
    public record Requisito(Integer idTarefa, String titulo, String descricao, Boolean concluido,
                            Boolean checkinDisponivel) {}
}
