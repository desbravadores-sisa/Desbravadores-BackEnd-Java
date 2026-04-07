package school.sptech.api_tasks.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class TarefaUpdateDto {

    @NotBlank
    private String nome;

    private String descricao;

    private Integer pontuacao;

    private LocalDateTime prazoEntrega;

    public TarefaUpdateDto() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(Integer pontuacao) {
        this.pontuacao = pontuacao;
    }

    public LocalDateTime getPrazoEntrega() {
        return prazoEntrega;
    }

    public void setPrazoEntrega(LocalDateTime prazoEntrega) {
        this.prazoEntrega = prazoEntrega;
    }
}
