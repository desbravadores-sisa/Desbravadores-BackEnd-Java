package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.Evidencia;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;
import school.sptech.APIDesbravadores.dto.EvidenciaAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.EvidenciaCriacaoDto;
import school.sptech.APIDesbravadores.dto.EvidenciaResponseDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.mapper.EvidenciaMapper;
import school.sptech.APIDesbravadores.repository.EvidenciaRepository;
import school.sptech.APIDesbravadores.repository.TarefaUnidadeRepository;

import java.util.List;

@Service
public class EvidenciaService {

    private final EvidenciaRepository evidenciaRepository;
    private final TarefaUnidadeRepository tarefaUnidadeRepository;

    public EvidenciaService(EvidenciaRepository evidenciaRepository, TarefaUnidadeRepository tarefaUnidadeRepository) {
        this.evidenciaRepository = evidenciaRepository;
        this.tarefaUnidadeRepository = tarefaUnidadeRepository;
    }

    @Transactional(readOnly = true)
    public List<EvidenciaResponseDto> findAll() {
        return evidenciaRepository.findAll().stream()
                .map(EvidenciaMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public EvidenciaResponseDto findById(Integer id) {
        Evidencia evidencia = evidenciaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Evidência não encontrada com ID: " + id));
        return EvidenciaMapper.toResponseDto(evidencia);
    }

    @Transactional
    public EvidenciaResponseDto create(EvidenciaCriacaoDto dto) {
        TarefaUnidade tarefaUnidade = tarefaUnidadeRepository.findById(dto.getIdTarefaUnidade())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("TarefaUnidade não encontrada com ID: " + dto.getIdTarefaUnidade()));
        Evidencia evidencia = EvidenciaMapper.toEntity(dto, tarefaUnidade);
        evidenciaRepository.save(evidencia);
        return EvidenciaMapper.toResponseDto(evidencia);
    }

    @Transactional
    public EvidenciaResponseDto update(Integer id, EvidenciaAtualizacaoDto dto) {
        Evidencia evidencia = evidenciaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Evidência não encontrada com ID: " + id));
        TarefaUnidade tarefaUnidade = tarefaUnidadeRepository.findById(dto.getIdTarefaUnidade())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("TarefaUnidade não encontrada com ID: " + dto.getIdTarefaUnidade()));
        EvidenciaMapper.updateEntity(dto, evidencia, tarefaUnidade);
        evidenciaRepository.save(evidencia);
        return EvidenciaMapper.toResponseDto(evidencia);
    }

    @Transactional
    public void delete(Integer id) {
        if (!evidenciaRepository.existsById(id)) {
            throw new EntidadeNaoEncontradaException("Evidência não encontrada com ID: " + id);
        }
        evidenciaRepository.deleteById(id);
    }
}
