package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.StatusKanban;
import school.sptech.APIDesbravadores.domain.Tarefa;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;
import school.sptech.APIDesbravadores.dto.TarefaCreateDto;
import school.sptech.APIDesbravadores.dto.TarefaResponseDto;
import school.sptech.APIDesbravadores.dto.TarefaUpdateDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.exception.RequisicaoInvalidaException;
import school.sptech.APIDesbravadores.mapper.TarefaMapper;
import school.sptech.APIDesbravadores.repository.TarefaRepository;
import school.sptech.APIDesbravadores.repository.TarefaUnidadeRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final TarefaUnidadeRepository tarefaUnidadeRepository;

    public TarefaService(TarefaRepository tarefaRepository, TarefaUnidadeRepository tarefaUnidadeRepository) {
        this.tarefaRepository = tarefaRepository;
        this.tarefaUnidadeRepository = tarefaUnidadeRepository;
    }

    @Transactional
    public TarefaResponseDto create(TarefaCreateDto dto) {
        Tarefa tarefa = TarefaMapper.toEntity(dto);
        Tarefa savedTarefa = tarefaRepository.save(tarefa);

        TarefaUnidade tu = new TarefaUnidade();
        tu.setTarefa(savedTarefa);
        tu.setFkUnidade(dto.getFkUnidade());
        tu.setStatusKanban(StatusKanban.A_FAZER);
        tarefaUnidadeRepository.save(tu);

        return TarefaMapper.toResponseDto(savedTarefa, tu);
    }

    @Transactional(readOnly = true)
    public List<TarefaResponseDto> findAll() {
        List<Tarefa> tarefas = tarefaRepository.findAll();
        if (tarefas.isEmpty()) {
            return List.of();
        }

        // Busca os vínculos de uma vez só. Consultar dentro do laço custava
        // uma query por tarefa (N+1).
        List<Integer> ids = tarefas.stream().map(Tarefa::getId).toList();
        Map<Integer, TarefaUnidade> vinculosPorTarefa = tarefaUnidadeRepository.findByTarefaIdIn(ids).stream()
                .collect(Collectors.toMap(
                        tu -> tu.getTarefa().getId(),
                        Function.identity(),
                        (existente, duplicado) -> existente));

        return tarefas.stream()
                .map(t -> TarefaMapper.toResponseDto(t, vinculosPorTarefa.get(t.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TarefaResponseDto findById(Integer id) {
        Tarefa t = tarefaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa não encontrada com ID: " + id));
        TarefaUnidade tu = tarefaUnidadeRepository.findByTarefaId(t.getId()).orElse(null);
        return TarefaMapper.toResponseDto(t, tu);
    }

    @Transactional
    public TarefaResponseDto update(Integer id, TarefaUpdateDto dto) {
        Tarefa t = tarefaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa não encontrada com ID: " + id));
        TarefaMapper.updateEntity(dto, t);
        Tarefa saved = tarefaRepository.save(t);
        TarefaUnidade tu = tarefaUnidadeRepository.findByTarefaId(saved.getId()).orElse(null);
        return TarefaMapper.toResponseDto(saved, tu);
    }

    @Transactional
    public void delete(Integer id) {
        Tarefa t = tarefaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa não encontrada com ID: " + id));

        tarefaUnidadeRepository.deleteByTarefaId(t.getId());
        tarefaRepository.delete(t);
    }

    public TarefaResponseDto findStatusByTarefaId(Integer id) {
        Tarefa t = tarefaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa não encontrada com ID: " + id));

        TarefaUnidade tu = tarefaUnidadeRepository.findByTarefaId(t.getId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("TarefaUnidade não encontrada para Tarefa ID: " + id));

        return TarefaMapper.toResponseDto(t, tu);
    }

    @Transactional
    public TarefaResponseDto updateStatus(Integer id, String statusStr) {
        Tarefa t = tarefaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa não encontrada com ID: " + id));

        TarefaUnidade tu = tarefaUnidadeRepository.findByTarefaId(t.getId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("TarefaUnidade não encontrada para Tarefa ID: " + id));

        StatusKanban status = StatusKanban.fromString(statusStr);
        if (status == null) {
            throw new RequisicaoInvalidaException("Status inválido: " + statusStr);
        }

        tu.setStatusKanban(status);
        tarefaUnidadeRepository.save(tu);

        return TarefaMapper.toResponseDto(t, tu);
    }

    @Transactional(readOnly = true)
    public Map<String, List<TarefaResponseDto>> getKanban() {
        List<TarefaResponseDto> all = findAll();
        if (all.isEmpty()) {
            return Map.of();
        }

        // Semeia as quatro colunas para o quadro nunca vir com coluna faltando
        // quando nenhuma tarefa está naquele status.
        Map<String, List<TarefaResponseDto>> kanban = new LinkedHashMap<>();
        for (StatusKanban status : StatusKanban.values()) {
            kanban.put(status.getDescricao(), new ArrayList<>());
        }

        for (TarefaResponseDto tarefa : all) {
            // Tarefa sem vínculo não tem status e portanto não pertence a nenhuma coluna.
            if (tarefa.getStatusKanban() == null) {
                continue;
            }
            kanban.computeIfAbsent(tarefa.getStatusKanban(), chave -> new ArrayList<>()).add(tarefa);
        }

        return kanban;
    }
}
