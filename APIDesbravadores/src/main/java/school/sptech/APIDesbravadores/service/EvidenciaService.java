package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.Evidencia;
import school.sptech.APIDesbravadores.domain.StatusKanban;
import school.sptech.APIDesbravadores.domain.TarefaUnidade;
import school.sptech.APIDesbravadores.dto.EvidenciaCreateDto;
import school.sptech.APIDesbravadores.dto.EvidenciaResponseDto;
import school.sptech.APIDesbravadores.dto.EvidenciaUpdateDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.exception.RequisicaoInvalidaException;
import school.sptech.APIDesbravadores.mapper.EvidenciaMapper;
import school.sptech.APIDesbravadores.repository.EvidenciaRepository;
import school.sptech.APIDesbravadores.repository.TarefaUnidadeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EvidenciaService {

    private final EvidenciaRepository evidenciaRepository;
    private final TarefaUnidadeRepository tarefaUnidadeRepository;

    public EvidenciaService(EvidenciaRepository evidenciaRepository, TarefaUnidadeRepository tarefaUnidadeRepository) {
        this.evidenciaRepository = evidenciaRepository;
        this.tarefaUnidadeRepository = tarefaUnidadeRepository;
    }

    @Transactional
    public EvidenciaResponseDto create(EvidenciaCreateDto dto, Integer idUnidade) {
        validarUnidadeDoConselheiro(idUnidade);
        TarefaUnidade tarefaUnidade = buscarTarefaUnidade(dto.getIdTarefa(), idUnidade);
        Evidencia evidencia = EvidenciaMapper.toEntity(dto, tarefaUnidade);
        Evidencia saved = evidenciaRepository.save(evidencia);
        return EvidenciaMapper.toResponseDto(saved);
    }

    public List<EvidenciaResponseDto> findAllByClube(Integer idClube) {
        return evidenciaRepository.findAllByTarefaUnidadeTarefaFkClube(idClube).stream()
                .map(EvidenciaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<EvidenciaResponseDto> findAllByUnidade(Integer idUnidade) {
        validarUnidadeDoConselheiro(idUnidade);
        return evidenciaRepository.findAllByTarefaUnidadeFkUnidade(idUnidade).stream()
                .map(EvidenciaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EvidenciaResponseDto update(Integer id, EvidenciaUpdateDto dto, Integer idUnidade) {
        validarUnidadeDoConselheiro(idUnidade);
        Evidencia evidencia = buscarEvidenciaDaUnidade(id, idUnidade);
        EvidenciaMapper.updateEntity(dto, evidencia);
        Evidencia saved = evidenciaRepository.save(evidencia);
        return EvidenciaMapper.toResponseDto(saved);
    }

    @Transactional
    public void delete(Integer id, Integer idUnidade) {
        validarUnidadeDoConselheiro(idUnidade);
        Evidencia evidencia = buscarEvidenciaDaUnidade(id, idUnidade);
        if (StatusKanban.CONCLUIDO.equals(evidencia.getTarefaUnidade().getStatusKanban())) {
            throw new RequisicaoInvalidaException("Não é possível deletar evidência de tarefa concluída");
        }
        evidenciaRepository.delete(evidencia);
    }

    private TarefaUnidade buscarTarefaUnidade(Integer idTarefa, Integer idUnidade) {
        return tarefaUnidadeRepository.findByTarefaIdAndFkUnidade(idTarefa, idUnidade)
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        "TarefaUnidade não encontrada para Tarefa ID: " + idTarefa + " e Unidade ID: " + idUnidade));
    }

    private Evidencia buscarEvidenciaDaUnidade(Integer id, Integer idUnidade) {
        return evidenciaRepository.findByIdAndTarefaUnidadeFkUnidade(id, idUnidade)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Evidência não encontrada com ID: " + id));
    }

    private void validarUnidadeDoConselheiro(Integer idUnidade) {
        if (idUnidade == null) {
            throw new RequisicaoInvalidaException("Conselheiro não possui unidade vinculada");
        }
    }
}
