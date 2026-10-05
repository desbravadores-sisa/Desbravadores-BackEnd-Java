package school.sptech.APIDesbravadores.domain;

public enum StatusKanban {
    A_FAZER("A FAZER"),
    EM_ANDAMENTO("EM ANDAMENTO"),
    EM_REVISAO("EM REVISAO"),
    CONCLUIDO("CONCLUIDA");

    private final String descricao;

    StatusKanban(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusKanban fromString(String text) {
        if (text == null) return null;
        text = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").replace('_', ' ');
        for (StatusKanban b : StatusKanban.values()) {
                if (b.descricao.equalsIgnoreCase(text)
                    || b.name().replace('_', ' ').equalsIgnoreCase(text)
                    || (b == CONCLUIDO && "CONCLUIDO".equalsIgnoreCase(text))) {
                return b;
            }
        }
        return null;
    }
}
