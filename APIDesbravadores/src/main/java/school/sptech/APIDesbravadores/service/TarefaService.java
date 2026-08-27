package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.Caderno;
import school.sptech.APIDesbravadores.domain.Ciclo;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.StatusKanban;
import school.sptech.APIDesbravadores.domain.Tarefa;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.dto.TarefaCreateDto;
import school.sptech.APIDesbravadores.dto.TarefaResponseDto;
import school.sptech.APIDesbravadores.dto.TarefaUpdateDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.exception.RequisicaoInvalidaException;
import school.sptech.APIDesbravadores.mapper.TarefaMapper;
import school.sptech.APIDesbravadores.repository.CadernoRepository;
import school.sptech.APIDesbravadores.repository.CicloRepository;
import school.sptech.APIDesbravadores.repository.ClubeRepository;
import school.sptech.APIDesbravadores.repository.TarefaRepository;
import school.sptech.APIDesbravadores.repository.TarefaUnidadeRepository;
import school.sptech.APIDesbravadores.repository.UnidadeRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final TarefaUnidadeRepository tarefaUnidadeRepository;
    private final ClubeRepository clubeRepository;
    private final UnidadeRepository unidadeRepository;
    private final CicloRepository cicloRepository;
    private final CadernoRepository cadernoRepository;

    public TarefaService(TarefaRepository tarefaRepository, TarefaUnidadeRepository tarefaUnidadeRepository,
                          ClubeRepository clubeRepository, UnidadeRepository unidadeRepository,
                          CicloRepository cicloRepository, CadernoRepository cadernoRepository) {
        this.tarefaRepository = tarefaRepository;
        this.tarefaUnidadeRepository = tarefaUnidadeRepository;
        this.clubeRepository = clubeRepository;
        this.unidadeRepository = unidadeRepository;
        this.cicloRepository = cicloRepository;
        this.cadernoRepository = cadernoRepository;
    }

    @Transactional
    public TarefaResponseDto create(TarefaCreateDto dto) {
        Clube clube = clubeRepository.findById(dto.getFkClube())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Clube não encontrado com ID: " + dto.getFkClube()));

        Caderno caderno = null;
        if (dto.getFkCaderno() != null) {
            caderno = cadernoRepository.findById(dto.getFkCaderno())
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Caderno não encontrado com ID: " + dto.getFkCaderno()));
        }

        Unidade unidade = unidadeRepository.findById(dto.getFkUnidade())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Unidade não encontrada com ID: " + dto.getFkUnidade()));

        Ciclo ciclo = cicloRepository.findByClubeIdAndAtivoTrue(dto.getFkClube())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Nenhum ciclo ativo encontrado para o Clube ID: " + dto.getFkClube()));

        Tarefa tarefa = TarefaMapper.toEntity(dto, clube, caderno);
        Tarefa savedTarefa = tarefaRepository.save(tarefa);

        TarefaUnidade tu = new TarefaUnidade();
        tu.setTarefa(savedTarefa);
        tu.setUnidade(unidade);
        tu.setCiclo(ciclo);
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

    public Map<String, List<TarefaResponseDto>> getKanban() {
        List<TarefaResponseDto> all = findAll();
        return all.stream()
                .collect(Collectors.groupingBy(TarefaResponseDto::getStatusKanban));
    }
}
