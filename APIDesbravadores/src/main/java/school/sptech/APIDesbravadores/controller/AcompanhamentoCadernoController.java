package school.sptech.APIDesbravadores.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import school.sptech.APIDesbravadores.dto.CheckinDto;
import school.sptech.APIDesbravadores.dto.CheckinMarcacaoDto;
import school.sptech.APIDesbravadores.dto.DesbravadorResponseDto;
import school.sptech.APIDesbravadores.dto.DesbravadorUnidadeDto;
import school.sptech.APIDesbravadores.dto.RequisitosDesbravadorDto;
import school.sptech.APIDesbravadores.dto.UsuarioDetalhesDto;
import school.sptech.APIDesbravadores.dto.VinculoCadernoCriacaoDto;
import school.sptech.APIDesbravadores.dto.VinculoCadernoDto;
import school.sptech.APIDesbravadores.service.AcompanhamentoCadernoService;

import java.util.List;

@RestController
@Tag(name = "Acompanhamento de cadernos", description = "Vínculo desbravador ↔ caderno, check-in e requisitos individuais")
public class AcompanhamentoCadernoController {

    private final AcompanhamentoCadernoService service;

    public AcompanhamentoCadernoController(AcompanhamentoCadernoService service) {
        this.service = service;
    }

    // ----- Diretoria: "Ver Caderno" -----

    @GetMapping("/cadernos/{idCaderno}/desbravadores")
    @PreAuthorize("hasRole('DIRETORIA')")
    @Operation(summary = "Desbravadores vinculados ao caderno, com requisitos cumpridos")
    public ResponseEntity<List<VinculoCadernoDto>> vinculados(@PathVariable Integer idCaderno,
                                                              @AuthenticationPrincipal UsuarioDetalhesDto u) {
        if (u == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(service.listarVinculados(idCaderno, u.getIdClube()));
    }

    @GetMapping("/cadernos/{idCaderno}/desbravadores/elegiveis")
    @PreAuthorize("hasRole('DIRETORIA')")
    @Operation(summary = "Desbravadores do clube que ainda não estão vinculados ao caderno")
    public ResponseEntity<List<DesbravadorResponseDto>> elegiveis(@PathVariable Integer idCaderno,
                                                                  @AuthenticationPrincipal UsuarioDetalhesDto u) {
        if (u == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(service.listarElegiveis(idCaderno, u.getIdClube()));
    }

    @PostMapping("/cadernos/{idCaderno}/desbravadores")
    @PreAuthorize("hasRole('DIRETORIA')")
    @Operation(summary = "Vincular desbravador ao caderno")
    public ResponseEntity<VinculoCadernoDto> vincular(@PathVariable Integer idCaderno,
                                                      @RequestBody @Valid VinculoCadernoCriacaoDto dto,
                                                      @AuthenticationPrincipal UsuarioDetalhesDto u) {
        if (u == null) return ResponseEntity.status(401).build();
        return ResponseEntity.status(201).body(service.vincular(idCaderno, dto.idDesbravador(), u.getIdClube()));
    }

    @DeleteMapping("/cadernos/{idCaderno}/desbravadores/{idDesbravador}")
    @PreAuthorize("hasRole('DIRETORIA')")
    @Operation(summary = "Desvincular desbravador do caderno")
    public ResponseEntity<Void> desvincular(@PathVariable Integer idCaderno, @PathVariable Integer idDesbravador,
                                            @AuthenticationPrincipal UsuarioDetalhesDto u) {
        if (u == null) return ResponseEntity.status(401).build();
        service.desvincular(idCaderno, idDesbravador, u.getIdClube());
        return ResponseEntity.noContent().build();
    }

    // ----- Conselheiro: check-in -----

    @GetMapping("/tarefas/{idTarefa}/checkin")
    @PreAuthorize("hasRole('CONSELHEIRO')")
    @Operation(summary = "Check-in da tarefa de caderno", description = "Crianças da unidade vinculadas ao caderno da tarefa e quem já cumpriu")
    public ResponseEntity<CheckinDto> checkin(@PathVariable Integer idTarefa, @AuthenticationPrincipal UsuarioDetalhesDto u) {
        if (u == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(service.obterCheckin(idTarefa, u.getIdUnidade(), u.getIdClube()));
    }

    @PutMapping("/tarefas/{idTarefa}/checkin/{idDesbravador}")
    @PreAuthorize("hasRole('CONSELHEIRO')")
    @Operation(summary = "Marcar/desmarcar criança no check-in",
            description = "Todas marcadas → tarefa vai para EM REVISAO (aguarda a Diretoria reconhecer); desmarcar volta para EM ANDAMENTO")
    public ResponseEntity<CheckinDto> marcar(@PathVariable Integer idTarefa, @PathVariable Integer idDesbravador,
                                             @RequestBody @Valid CheckinMarcacaoDto dto,
                                             @AuthenticationPrincipal UsuarioDetalhesDto u) {
        if (u == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(service.marcarCheckin(idTarefa, idDesbravador, dto.concluiu(), u.getIdUnidade(), u.getIdClube()));
    }

    // ----- Conselheiro: "Desbravadores" -----

    @GetMapping("/desbravadores/minha-unidade")
    @PreAuthorize("hasRole('CONSELHEIRO')")
    @Operation(summary = "Desbravadores da unidade do conselheiro, com progresso por caderno")
    public ResponseEntity<List<DesbravadorUnidadeDto>> minhaUnidade(@AuthenticationPrincipal UsuarioDetalhesDto u) {
        if (u == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(service.desbravadoresDaUnidade(u.getIdUnidade()));
    }

    @GetMapping("/desbravadores/{idDesbravador}/requisitos")
    @PreAuthorize("hasRole('CONSELHEIRO')")
    @Operation(summary = "Requisitos individuais de um desbravador da unidade do conselheiro")
    public ResponseEntity<RequisitosDesbravadorDto> requisitos(@PathVariable Integer idDesbravador,
                                                               @AuthenticationPrincipal UsuarioDetalhesDto u) {
        if (u == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(service.requisitosDoDesbravador(idDesbravador, u.getIdUnidade(), u.getIdClube()));
    }
}
