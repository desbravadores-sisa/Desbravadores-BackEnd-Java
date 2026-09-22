package school.sptech.APIDesbravadores.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CadernoDetalhesDto {
    private Integer idCaderno;
    private String nome;
    private Integer idadeAlvo;
    private Integer totalVinculados;
    private Integer totalRequisitos;

    public CadernoDetalhesDto(Integer idCaderno, String nome, Integer idadeAlvo, Integer totalVinculados, Integer totalRequisitos) {
        this.idCaderno = idCaderno;
        this.nome = nome;
        this.idadeAlvo = idadeAlvo;
        this.totalVinculados = totalVinculados;
        this.totalRequisitos = totalRequisitos;
    }
}
