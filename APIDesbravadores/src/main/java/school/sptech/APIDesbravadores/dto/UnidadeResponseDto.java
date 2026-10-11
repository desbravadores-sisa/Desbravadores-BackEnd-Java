package school.sptech.APIDesbravadores.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UnidadeResponseDto {

    private Integer id;

    private String nome;

    private String genero;

    private Integer idadeMinima;

    private Integer idadeMaxima;

    private Integer pontuacao;

    private String nomeConselheiro;

    private Integer tarefasConcluidas;

    private Integer totalTarefas;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Integer getIdadeMinima() {
        return idadeMinima;
    }

    public void setIdadeMinima(Integer idadeMinima) {
        this.idadeMinima = idadeMinima;
    }

    public Integer getIdadeMaxima() {
        return idadeMaxima;
    }

    public void setIdadeMaxima(Integer idadeMaxima) {
        this.idadeMaxima = idadeMaxima;
    }

    public Integer getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(Integer pontuacao) {
        this.pontuacao = pontuacao;
    }

    public String getNomeConselheiro() {
        return nomeConselheiro;
    }

    public void setNomeConselheiro(String nomeConselheiro) {
        this.nomeConselheiro = nomeConselheiro;
    }

    public Integer getTarefasConcluidas() {
        return tarefasConcluidas;
    }

    public void setTarefasConcluidas(Integer tarefasConcluidas) {
        this.tarefasConcluidas = tarefasConcluidas;
    }

    public Integer getTotalTarefas() {
        return totalTarefas;
    }

    public void setTotalTarefas(Integer totalTarefas) {
        this.totalTarefas = totalTarefas;
    }
}
