package school.sptech.APIDesbravadores.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Desbravador vinculado a um caderno ("Ver Caderno"), com os requisitos já cumpridos. */
public record VinculoCadernoDto(
        Integer idDesbravador,
        String nome,
        Integer idUnidade,
        String nomeUnidade,
        LocalDate dataNascimento,
        LocalDateTime dataVinculo,
        LocalDateTime dataConclusao,
        List<Integer> requisitosConcluidos
) {}
