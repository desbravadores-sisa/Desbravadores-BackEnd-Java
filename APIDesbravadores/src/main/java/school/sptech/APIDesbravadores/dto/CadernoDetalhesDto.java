package school.sptech.APIDesbravadores.dto;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
public class CadernoDetalhesDto {
    private Integer idCaderno;
    private String nome;
    private Integer idadeAlvo;
    private Integer totalVinculados;
    private Integer totalRequisitos;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String status;

    public CadernoDetalhesDto(Integer idCaderno, String nome, Integer idadeAlvo, Integer totalVinculados, Integer totalRequisitos, LocalDate dataInicio, LocalDate dataFim, String status) {
        this.idCaderno = idCaderno;
        this.nome = nome;
        this.idadeAlvo = idadeAlvo;
        this.totalVinculados = totalVinculados;
        this.totalRequisitos = totalRequisitos;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
    }
}
