package school.sptech.APIDesbravadores.dto;

import java.time.LocalDateTime;

/** Caderno vinculado a um desbravador; dataConclusao != null = caderno concluído (histórico). */
public record CadernoDoDesbravadorDto(Integer idCaderno, String nome, Integer idadeAlvo,
                                      LocalDateTime dataConclusao, Integer totalRequisitos,
                                      Integer requisitosConcluidos) {}
