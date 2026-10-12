package school.sptech.APIDesbravadores.dto;

import java.time.LocalDateTime;

public record PenalidadeDto(Integer id, Integer idUnidade, Integer idCiclo, Integer pontos, String motivo,
                            LocalDateTime dataRegistro, String autor) {}
