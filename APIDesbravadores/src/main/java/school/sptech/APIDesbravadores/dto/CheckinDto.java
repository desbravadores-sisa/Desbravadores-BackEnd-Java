package school.sptech.APIDesbravadores.dto;

import java.util.List;

/** Check-in de uma tarefa de caderno na unidade do conselheiro. */
public record CheckinDto(
        Integer idTarefa,
        String titulo,
        Integer idCaderno,
        String nomeCaderno,
        String statusKanban,
        Integer total,
        Integer marcados,
        List<Item> desbravadores
) {
    public record Item(Integer idDesbravador, String nome, Boolean concluiu) {}
}
