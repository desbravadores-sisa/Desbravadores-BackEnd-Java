package school.sptech.APIDesbravadores.domain;

public enum StatusKanban {
    A_FAZER("A FAZER"),
    EM_ANDAMENTO("EM ANDAMENTO"),
    EM_REVISAO("EM REVISAO"),
    CONCLUIDA("CONCLUIDA");

    private final String descricao;

    StatusKanban(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusKanban fromString(String text) {
        for (StatusKanban b : StatusKanban.values()) {
            if (b.descricao.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
