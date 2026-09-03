package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.Caderno;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.dto.CadernoAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.CadernoCriacaoDto;
import school.sptech.APIDesbravadores.dto.CadernoResponseDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.mapper.CadernoMapper;
import school.sptech.APIDesbravadores.repository.CadernoRepository;
import school.sptech.APIDesbravadores.repository.ClubeRepository;

import java.util.List;

@Service
public class CadernoService {

    private final CadernoRepository cadernoRepository;
    private final ClubeRepository clubeRepository;

    public CadernoService(CadernoRepository cadernoRepository, ClubeRepository clubeRepository) {
        this.cadernoRepository = cadernoRepository;
        this.clubeRepository = clubeRepository;
    }

    @Transactional
    public CadernoResponseDto create(CadernoCriacaoDto dto) {
        Clube clube = buscarClube(dto.getIdClube());
        Caderno caderno = CadernoMapper.toEntity(dto, clube);
        return CadernoMapper.toResponse(cadernoRepository.save(caderno));
    }

    @Transactional(readOnly = true)
    public List<CadernoResponseDto> findAll(Integer idClube) {
        List<Caderno> cadernos = idClube != null
                ? cadernoRepository.findByClubeId(idClube)
                : cadernoRepository.findAll();
        return CadernoMapper.toResponse(cadernos);
    }

    @Transactional(readOnly = true)
    public CadernoResponseDto findById(Integer id) {
        Caderno caderno = cadernoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Caderno não encontrado com ID: " + id));
        return CadernoMapper.toResponse(caderno);
    }

    @Transactional
    public CadernoResponseDto update(Integer id, CadernoAtualizacaoDto dto) {
        Caderno caderno = cadernoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Caderno não encontrado com ID: " + id));

        Clube clube = buscarClube(dto.getIdClube());

        caderno.setNome(dto.getNome());
        caderno.setIdadeAlvo(dto.getIdadeAlvo());
        caderno.setClube(clube);

        return CadernoMapper.toResponse(cadernoRepository.save(caderno));
    }

    @Transactional
    public void delete(Integer id) {
        if (!cadernoRepository.existsById(id)) {
            throw new EntidadeNaoEncontradaException("Caderno não encontrado com ID: " + id);
        }
        cadernoRepository.deleteById(id);
    }

    private Clube buscarClube(Integer idClube) {
        return clubeRepository.findById(idClube)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Clube não encontrado com ID: " + idClube));
    }
}
