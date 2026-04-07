package school.sptech.api_tasks.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.api_tasks.dto.TarefaCreateDto;
import school.sptech.api_tasks.dto.TarefaResponseDto;
import school.sptech.api_tasks.dto.TarefaUpdateDto;
import school.sptech.api_tasks.service.TarefaService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @PostMapping
    public ResponseEntity<TarefaResponseDto> create(@RequestBody @Valid TarefaCreateDto dto) {
        TarefaResponseDto response = tarefaService.create(dto);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TarefaResponseDto>> findAll() {
        List<TarefaResponseDto> response = tarefaService.findAll();
        if (response.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarefaResponseDto> findById(@PathVariable Integer id) {
        TarefaResponseDto response = tarefaService.findById(id);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefaResponseDto> update(@PathVariable Integer id, @RequestBody @Valid TarefaUpdateDto dto) {
        TarefaResponseDto response = tarefaService.update(id, dto);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        tarefaService.delete(id);
        return ResponseEntity.status(204).build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TarefaResponseDto> updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null) {
            return ResponseEntity.status(400).build();
        }
        TarefaResponseDto response = tarefaService.updateStatus(id, status);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/kanban")
    public ResponseEntity<Map<String, List<TarefaResponseDto>>> getKanban() {
        Map<String, List<TarefaResponseDto>> response = tarefaService.getKanban();
        if (response.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.status(200).body(response);
    }
}
