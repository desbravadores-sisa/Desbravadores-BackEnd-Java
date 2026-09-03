package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.ChecklistCaderno;
import school.sptech.APIDesbravadores.domain.Desbravador;
import school.sptech.APIDesbravadores.domain.ExecucaoCaderno;
import school.sptech.APIDesbravadores.dto.ChecklistCadernoAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.ChecklistCadernoCriacaoDto;
import school.sptech.APIDesbravadores.dto.ChecklistCadernoResponseDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.mapper.ChecklistCadernoMapper;
import school.sptech.APIDesbravadores.repository.ChecklistCadernoRepository;
import school.sptech.APIDesbravadores.repository.DesbravadorRepository;
import school.sptech.APIDesbravadores.repository.ExecucaoCadernoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChecklistCadernoService {

    private final ChecklistCadernoRepository checklistCadernoRepository;
    private final ExecucaoCadernoRepository execucaoCadernoRepository;
    private final DesbravadorRepository desbravadorRepository;

    public ChecklistCadernoService(ChecklistCadernoRepository checklistCadernoRepository,
                                   ExecucaoCadernoRepository execucaoCadernoRepository,
                                   DesbravadorRepository desbravadorRepository) {
        this.checklistCadernoRepository = checklistCadernoRepository;
        this.execucaoCadernoRepository = execucaoCadernoRepository;
        this.desbravadorRepository = desbravadorRepository;
    }

    @Transactional(readOnly = true)
    public List<ChecklistCadernoResponseDto> findAll() {
        return checklistCadernoRepository.findAll().stream()
                .map(ChecklistCadernoMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChecklistCadernoResponseDto findById(Integer id) {
        ChecklistCaderno checklist = checklistCadernoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Checklist não encontrado com ID: " + id));
        return ChecklistCadernoMapper.toResponseDto(checklist);
    }

    @Transactional
    public ChecklistCadernoResponseDto create(ChecklistCadernoCriacaoDto dto) {
        ExecucaoCaderno execucaoCaderno = buscarExecucao(dto.getIdExecucaoCaderno());
        Desbravador desbravador = desbravadorRepository.findById(dto.getIdDesbravador())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Desbravador não encontrado com ID: " + dto.getIdDesbravador()));
        ChecklistCaderno checklist = ChecklistCadernoMapper.toEntity(dto, execucaoCaderno, desbravador);
        if (Boolean.TRUE.equals(checklist.getConcluiuTarefa())) {
            checklist.setDataMarcacao(LocalDateTime.now());
        }
        checklistCadernoRepository.save(checklist);
        return ChecklistCadernoMapper.toResponseDto(checklist);
    }

    @Transactional
    public ChecklistCadernoResponseDto update(Integer id, ChecklistCadernoAtualizacaoDto dto) {
        ChecklistCaderno checklist = checklistCadernoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Checklist não encontrado com ID: " + id));
        ExecucaoCaderno execucaoCaderno = buscarExecucao(dto.getIdExecucaoCaderno());
        Desbravador desbravador = desbravadorRepository.findById(dto.getIdDesbravador())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Desbravador não encontrado com ID: " + dto.getIdDesbravador()));
        checklist.setExecucaoCaderno(execucaoCaderno);
        checklist.setDesbravador(desbravador);
        checklist.setConcluiuTarefa(dto.getConcluiuTarefa());
        if (Boolean.TRUE.equals(dto.getConcluiuTarefa())) {
            checklist.setDataMarcacao(LocalDateTime.now());
        } else {
            checklist.setDataMarcacao(null);
        }
        checklistCadernoRepository.save(checklist);
        return ChecklistCadernoMapper.toResponseDto(checklist);
    }

    @Transactional
    public void delete(Integer id) {
        if (!checklistCadernoRepository.existsById(id)) {
            throw new EntidadeNaoEncontradaException("Checklist não encontrado com ID: " + id);
        }
        checklistCadernoRepository.deleteById(id);
    }

    private ExecucaoCaderno buscarExecucao(Integer id) {
        return execucaoCadernoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Execução de caderno não encontrada com ID: " + id));
    }
}
