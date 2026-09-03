package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.dto.ClubeAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.ClubeCriacaoDto;
import school.sptech.APIDesbravadores.dto.ClubeResponseDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.mapper.ClubeMapper;
import school.sptech.APIDesbravadores.repository.ClubeRepository;

import java.util.List;

@Service
public class ClubeService {

    private final ClubeRepository clubeRepository;

    public ClubeService(ClubeRepository clubeRepository) {
        this.clubeRepository = clubeRepository;
    }

    @Transactional
    public ClubeResponseDto create(ClubeCriacaoDto dto) {
        Clube clube = ClubeMapper.toEntity(dto);
        Clube saved = clubeRepository.save(clube);
        return ClubeMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ClubeResponseDto> findAll() {
        return ClubeMapper.toResponse(clubeRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ClubeResponseDto findById(Integer id) {
        Clube clube = clubeRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Clube não encontrado com ID: " + id));
        return ClubeMapper.toResponse(clube);
    }

    @Transactional
    public ClubeResponseDto update(Integer id, ClubeAtualizacaoDto dto) {
        Clube clube = clubeRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Clube não encontrado com ID: " + id));
        ClubeMapper.updateEntity(dto, clube);
        Clube saved = clubeRepository.save(clube);
        return ClubeMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Integer id) {
        Clube clube = clubeRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Clube não encontrado com ID: " + id));
        clubeRepository.delete(clube);
    }
}
