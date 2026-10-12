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
        if (text == null) {
            return null;
        }
        // Aceita "EM REVISAO", "Em revisão", "em_revisao", "Concluído" etc.
        String normalizado = java.text.Normalizer.normalize(text.trim(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('_', ' ')
                .toUpperCase(java.util.Locale.ROOT);
        if ("CONCLUIDO".equals(normalizado)) {
            return CONCLUIDA;
        }
        for (StatusKanban b : StatusKanban.values()) {
            if (b.descricao.equals(normalizado)) {
                return b;
            }
        }
        return null;
    }
}
