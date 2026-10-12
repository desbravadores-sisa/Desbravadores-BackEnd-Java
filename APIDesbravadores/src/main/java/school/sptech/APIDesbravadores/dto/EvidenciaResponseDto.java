package school.sptech.APIDesbravadores.dto;



import lombok.Getter;

import lombok.Setter;

import lombok.ToString;



import java.time.LocalDateTime;



@Getter

@Setter

@ToString

public class EvidenciaResponseDto {



    private Integer id;



    private Integer idTarefaUnidade;



    private String urlS3;



    private String comentarioFeedback;



    private LocalDateTime dataEnvio;



    // Campos derivados de Unidade_Tarefa -> Tarefa/Unidade, consumidos pela tela de Evidências

    private Integer idTarefa;



    private String titulo;



    private String descricao;



    /** Pontos efetivos: concedidos pela Diretoria ou, se não houver, os da tarefa. */
    private Integer pontuacao;

    /** Pontos definidos na tarefa. */
    private Integer pontuacaoPrevista;



    private String tipoEvidencia;



    private Integer fkCaderno;



    private Integer idUnidade;



    private String nomeUnidade;

    /** Conselheiro responsável pela unidade (vw_resumo_unidades). */
    private String nomeConselheiro;



    private String statusKanban;



    private String status;

}

