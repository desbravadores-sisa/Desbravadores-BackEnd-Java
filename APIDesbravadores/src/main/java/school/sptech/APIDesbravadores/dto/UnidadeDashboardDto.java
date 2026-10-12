package school.sptech.APIDesbravadores.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Dashboard da unidade (tela "Ver Detalhes" da Diretoria).
 * "Descontos" = penalidades lançadas pela Diretoria (tabela Penalidade) no ciclo ativo.
 */
public record UnidadeDashboardDto(
        UnidadeInfo unidade,
        Integer pontuacaoTotal,
        Integer pontosCaderno,
        Semana semanaAtual,
        Integer evidenciasPendentes,
        List<Semana> semanas,
        List<Atividade> atividades,
        List<PenalidadeDto> penalidades
) {
    public record UnidadeInfo(Integer id, String nome, String genero, Integer idadeMinima,
                              Integer idadeMaxima, String nomeConselheiro) {}

    public record Semana(String rotulo, LocalDate inicio, LocalDate fim, Integer ganhos, Integer descontos) {}

    public record Atividade(Integer idTarefaUnidade, Integer idTarefa, String titulo, String tipo,
                            Integer idCaderno, LocalDate prazo, Integer pontos, String status) {}
}
