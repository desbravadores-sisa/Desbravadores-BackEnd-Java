package school.sptech.APIDesbravadores.dto;



import jakarta.validation.constraints.Size;

import lombok.Getter;

import lombok.Setter;

import lombok.ToString;



/**

 * Atualização parcial: apenas os campos não nulos são aplicados.

 * {@code status} é a decisão da diretoria (APROVADA, RECONHECIDO,

 * CORRECAO_SOLICITADA, PENDENTE) e é refletido em Unidade_Tarefa.status_kanban.

 */

@Getter

@Setter

@ToString

public class EvidenciaAtualizacaoDto {



    private Integer idTarefaUnidade;



    @Size(max = 500)

    private String urlS3;



    @Size(max = 1000)

    private String comentarioFeedback;



    private String status;

    /** Pontos concedidos na aprovação; se nulo, vale Tarefa.pontuacao. */
    @jakarta.validation.constraints.PositiveOrZero
    private Integer pontuacao;

}

