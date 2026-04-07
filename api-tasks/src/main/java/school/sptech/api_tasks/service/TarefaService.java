package school.sptech.api_tasks.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.api_tasks.domain.StatusKanban;
import school.sptech.api_tasks.domain.Tarefa;
import school.sptech.api_tasks.domain.TarefaUnidade;
import school.sptech.api_tasks.dto.TarefaCreateDto;
import school.sptech.api_tasks.dto.TarefaResponseDto;
import school.sptech.api_tasks.dto.TarefaUpdateDto;
import school.sptech.api_tasks.exception.EntidadeNaoEncontradaException;
import school.sptech.api_tasks.mapper.TarefaMapper;
import school.sptech.api_tasks.repository.TarefaRepository;
import school.sptech.api_tasks.repository.TarefaUnidadeRepository;

import java.util.List;
import java.util.Map;
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

    public List<TarefaResponseDto> findAll() {
        return tarefaRepository.findAll().stream()
                .map(t -> {
                    TarefaUnidade tu = tarefaUnidadeRepository.findByTarefaId(t.getId()).orElse(null);
                    return TarefaMapper.toResponseDto(t, tu);
                })
                .collect(Collectors.toList());
    }

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

        tarefaUnidadeRepository.findByTarefaId(t.getId()).ifPresent(tarefaUnidadeRepository::delete);
        tarefaRepository.delete(t);
    }

    @Transactional
    public TarefaResponseDto updateStatus(Integer id, String statusStr) {
        Tarefa t = tarefaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa não encontrada com ID: " + id));

        TarefaUnidade tu = tarefaUnidadeRepository.findByTarefaId(t.getId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("TarefaUnidade não encontrada para Tarefa ID: " + id));

        StatusKanban status = StatusKanban.fromString(statusStr);
        if (status == null) {
            throw new IllegalArgumentException("Status inválido: " + statusStr);
        }

        tu.setStatusKanban(status);
        tarefaUnidadeRepository.save(tu);

        return TarefaMapper.toResponseDto(t, tu);
    }

    public Map<String, List<TarefaResponseDto>> getKanban() {
        List<TarefaResponseDto> all = findAll();
        return all.stream()
                .collect(Collectors.groupingBy(TarefaResponseDto::getStatusKanban));
    }
}
