package school.sptech.api_tasks.domain;

public enum StatusKanban {
    A_FAZER("A fazer"),
    EM_ANDAMENTO("Em andamento"),
    EM_REVISAO("Em revisão"),
    CONCLUIDO("Concluído");

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
