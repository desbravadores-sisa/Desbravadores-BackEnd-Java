package school.sptech.APIDesbravadores.service;

import java.util.List;

public record EvidenciaEnviadaEvent(Integer entrega, String tarefa, String unidade, List<String> destinatarios) {}
