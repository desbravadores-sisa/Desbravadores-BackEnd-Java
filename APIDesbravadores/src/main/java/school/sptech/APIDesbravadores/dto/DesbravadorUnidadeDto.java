package school.sptech.APIDesbravadores.dto;

import java.time.LocalDate;
import java.util.List;

/** Desbravador da unidade do conselheiro, com os cadernos e o progresso em cada um. */
public record DesbravadorUnidadeDto(Integer id, String nome, LocalDate dataNascimento,
                                    List<CadernoDoDesbravadorDto> cadernos) {}
