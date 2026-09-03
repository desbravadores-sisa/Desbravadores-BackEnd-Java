package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.Ciclo;
import school.sptech.APIDesbravadores.domain.ExecucaoCaderno;
import school.sptech.APIDesbravadores.domain.StatusKanban;
import school.sptech.APIDesbravadores.domain.Tarefa;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.dto.ExecucaoCadernoAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.ExecucaoCadernoCriacaoDto;
import school.sptech.APIDesbravadores.dto.ExecucaoCadernoResponseDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.exception.RequisicaoInvalidaException;
import school.sptech.APIDesbravadores.mapper.ExecucaoCadernoMapper;
import school.sptech.APIDesbravadores.repository.CicloRepository;
import school.sptech.APIDesbravadores.repository.ExecucaoCadernoRepository;
import school.sptech.APIDesbravadores.repository.TarefaRepository;
import school.sptech.APIDesbravadores.repository.UnidadeRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExecucaoCadernoService {

    private final ExecucaoCadernoRepository execucaoCadernoRepository;
    private final UnidadeRepository unidadeRepository;
    private final TarefaRepository tarefaRepository;
    private final CicloRepository cicloRepository;

    public ExecucaoCadernoService(ExecucaoCadernoRepository execucaoCadernoRepository,
                                  UnidadeRepository unidadeRepository,
                                  TarefaRepository tarefaRepository,
                                  CicloRepository cicloRepository) {
        this.execucaoCadernoRepository = execucaoCadernoRepository;
        this.unidadeRepository = unidadeRepository;
        this.tarefaRepository = tarefaRepository;
        this.cicloRepository = cicloRepository;
    }

    @Transactional(readOnly = true)
    public List<ExecucaoCadernoResponseDto> findAll() {
        return execucaoCadernoRepository.findAll().stream()
                .map(ExecucaoCadernoMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExecucaoCadernoResponseDto findById(Integer id) {
        ExecucaoCaderno execucao = execucaoCadernoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Execução de caderno não encontrada com ID: " + id));
        return ExecucaoCadernoMapper.toResponseDto(execucao);
    }

    @Transactional
    public ExecucaoCadernoResponseDto create(ExecucaoCadernoCriacaoDto dto) {
        Unidade unidade = buscarUnidade(dto.getIdUnidade());
        Tarefa tarefa = buscarTarefa(dto.getIdTarefa());
        Ciclo ciclo = buscarCiclo(dto.getIdCiclo());
        ExecucaoCaderno execucao = ExecucaoCadernoMapper.toEntity(dto, unidade, tarefa, ciclo);
        if (execucao.getStatusKanban() == null) {
            throw new RequisicaoInvalidaException("Status inválido: " + dto.getStatusKanban());
        }
        if (StatusKanban.CONCLUIDA.equals(execucao.getStatusKanban())) {
            execucao.setDataConclusao(LocalDateTime.now());
        }
        execucaoCadernoRepository.save(execucao);
        return ExecucaoCadernoMapper.toResponseDto(execucao);
    }

    @Transactional
    public ExecucaoCadernoResponseDto update(Integer id, ExecucaoCadernoAtualizacaoDto dto) {
        ExecucaoCaderno execucao = execucaoCadernoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Execução de caderno não encontrada com ID: " + id));
        Unidade unidade = buscarUnidade(dto.getIdUnidade());
        Tarefa tarefa = buscarTarefa(dto.getIdTarefa());
        Ciclo ciclo = buscarCiclo(dto.getIdCiclo());
        StatusKanban status = StatusKanban.fromString(dto.getStatusKanban());
        if (status == null) {
            throw new RequisicaoInvalidaException("Status inválido: " + dto.getStatusKanban());
        }
        execucao.setUnidade(unidade);
        execucao.setTarefa(tarefa);
        execucao.setCiclo(ciclo);
        execucao.setStatusKanban(status);
        if (StatusKanban.CONCLUIDA.equals(status) && execucao.getDataConclusao() == null) {
            execucao.setDataConclusao(LocalDateTime.now());
        }
        execucaoCadernoRepository.save(execucao);
        return ExecucaoCadernoMapper.toResponseDto(execucao);
    }

    @Transactional
    public void delete(Integer id) {
        if (!execucaoCadernoRepository.existsById(id)) {
            throw new EntidadeNaoEncontradaException("Execução de caderno não encontrada com ID: " + id);
        }
        execucaoCadernoRepository.deleteById(id);
    }

    private Unidade buscarUnidade(Integer id) {
        return unidadeRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Unidade não encontrada com ID: " + id));
    }

    private Tarefa buscarTarefa(Integer id) {
        return tarefaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa não encontrada com ID: " + id));
    }

    private Ciclo buscarCiclo(Integer id) {
        return cicloRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Ciclo não encontrado com ID: " + id));
    }
}
