package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.Desbravador;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.dto.DesbravadorAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.DesbravadorCriacaoDto;
import school.sptech.APIDesbravadores.dto.DesbravadorResponseDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.mapper.DesbravadorMapper;
import school.sptech.APIDesbravadores.repository.ClubeRepository;
import school.sptech.APIDesbravadores.repository.DesbravadorRepository;
import school.sptech.APIDesbravadores.repository.UnidadeRepository;

import java.util.List;

@Service
public class DesbravadorService {

    private final DesbravadorRepository desbravadorRepository;
    private final ClubeRepository clubeRepository;
    private final UnidadeRepository unidadeRepository;

    public DesbravadorService(DesbravadorRepository desbravadorRepository,
                              ClubeRepository clubeRepository,
                              UnidadeRepository unidadeRepository) {
        this.desbravadorRepository = desbravadorRepository;
        this.clubeRepository = clubeRepository;
        this.unidadeRepository = unidadeRepository;
    }

    @Transactional
    public DesbravadorResponseDto create(DesbravadorCriacaoDto dto) {
        Clube clube = buscarClube(dto.getIdClube());
        Unidade unidade = buscarUnidade(dto.getIdUnidade());
        Desbravador desbravador = DesbravadorMapper.toEntity(dto, clube, unidade);
        return DesbravadorMapper.toResponse(desbravadorRepository.save(desbravador));
    }

    @Transactional(readOnly = true)
    public List<DesbravadorResponseDto> findAll(Integer idClube) {
        List<Desbravador> desbravadores = idClube != null
                ? desbravadorRepository.findByClubeId(idClube)
                : desbravadorRepository.findAll();
        return DesbravadorMapper.toResponse(desbravadores);
    }

    @Transactional(readOnly = true)
    public DesbravadorResponseDto findById(Integer id) {
        Desbravador desbravador = desbravadorRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Desbravador não encontrado com ID: " + id));
        return DesbravadorMapper.toResponse(desbravador);
    }

    @Transactional
    public DesbravadorResponseDto update(Integer id, DesbravadorAtualizacaoDto dto) {
        Desbravador desbravador = desbravadorRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Desbravador não encontrado com ID: " + id));

        Clube clube = buscarClube(dto.getIdClube());
        Unidade unidade = buscarUnidade(dto.getIdUnidade());

        desbravador.setNome(dto.getNome());
        desbravador.setDataNascimento(dto.getDataNascimento());
        desbravador.setGenero(dto.getGenero());
        desbravador.setAtivo(dto.getAtivo());
        desbravador.setClube(clube);
        desbravador.setUnidade(unidade);

        return DesbravadorMapper.toResponse(desbravadorRepository.save(desbravador));
    }

    @Transactional
    public void delete(Integer id) {
        if (!desbravadorRepository.existsById(id)) {
            throw new EntidadeNaoEncontradaException("Desbravador não encontrado com ID: " + id);
        }
        desbravadorRepository.deleteById(id);
    }

    private Clube buscarClube(Integer idClube) {
        return clubeRepository.findById(idClube)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Clube não encontrado com ID: " + idClube));
    }

    private Unidade buscarUnidade(Integer idUnidade) {
        return unidadeRepository.findById(idUnidade)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Unidade não encontrada com ID: " + idUnidade));
    }
}
