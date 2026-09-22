package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "vw_resumo_cadernos")
@Immutable
@Getter
@ToString
public class ResumoCadernoView {

    @Id
    @Column(name = "id_caderno")
    private Integer idCaderno;

    @Column(name = "id_clube")
    private Integer idClube;

    @Column(name = "nome_caderno")
    private String nomeCaderno;

    @Column(name = "idade_alvo")
    private Integer idadeAlvo;

    @Column(name = "nome_grupo")
    private String nomeGrupo;

    @Column(name = "total_requisitos")
    private Integer totalRequisitos;

    @Column(name = "total_vinculados")
    private Integer totalVinculados;
}
