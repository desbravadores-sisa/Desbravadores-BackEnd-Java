package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDateTime;

@Entity
@Table(name = "tarefa")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarefa")
    private Integer id;

    @Column(name = "fk_clube")
    private Integer fkClube;

    private String nome;

    private String descricao;

    private Integer pontuacao;

    @Column(name = "prazo_entrega")
    private LocalDateTime prazoEntrega;

    // Quem preenche é o DEFAULT do banco. O @Generated faz o Hibernate reler o valor
    // depois do INSERT, senão a resposta do POST sai com dataCriacao nula.
    @Generated(event = EventType.INSERT)
    @Column(name = "data_criacao", insertable = false, updatable = false)
    private LocalDateTime dataCriacao;

    public Tarefa() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFkClube() {
        return fkClube;
    }

    public void setFkClube(Integer fkClube) {
        this.fkClube = fkClube;
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

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
