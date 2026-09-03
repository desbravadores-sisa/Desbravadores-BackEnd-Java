package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.Ciclo;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.dto.CicloAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.CicloCriacaoDto;
import school.sptech.APIDesbravadores.dto.CicloResponseDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.mapper.CicloMapper;
import school.sptech.APIDesbravadores.repository.CicloRepository;
import school.sptech.APIDesbravadores.repository.ClubeRepository;

import java.util.List;

@Service
public class CicloService {

    private final CicloRepository cicloRepository;
    private final ClubeRepository clubeRepository;

    public CicloService(CicloRepository cicloRepository, ClubeRepository clubeRepository) {
        this.cicloRepository = cicloRepository;
        this.clubeRepository = clubeRepository;
    }

    @Transactional
    public CicloResponseDto create(CicloCriacaoDto dto) {
        Clube clube = buscarClube(dto.getIdClube());
        Ciclo ciclo = CicloMapper.toEntity(dto, clube);
        return CicloMapper.toResponse(cicloRepository.save(ciclo));
    }

    @Transactional(readOnly = true)
    public List<CicloResponseDto> findAll(Integer idClube) {
        List<Ciclo> ciclos = idClube != null
                ? cicloRepository.findByClubeId(idClube)
                : cicloRepository.findAll();
        return CicloMapper.toResponse(ciclos);
    }

    @Transactional(readOnly = true)
    public CicloResponseDto findById(Integer id) {
        Ciclo ciclo = cicloRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Ciclo não encontrado com ID: " + id));
        return CicloMapper.toResponse(ciclo);
    }

    @Transactional
    public CicloResponseDto update(Integer id, CicloAtualizacaoDto dto) {
        Ciclo ciclo = cicloRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Ciclo não encontrado com ID: " + id));

        Clube clube = buscarClube(dto.getIdClube());

        ciclo.setNome(dto.getNome());
        ciclo.setDataInicio(dto.getDataInicio());
        ciclo.setDataFim(dto.getDataFim());
        ciclo.setAtivo(dto.getAtivo());
        ciclo.setClube(clube);

        return CicloMapper.toResponse(cicloRepository.save(ciclo));
    }

    @Transactional
    public void delete(Integer id) {
        if (!cicloRepository.existsById(id)) {
            throw new EntidadeNaoEncontradaException("Ciclo não encontrado com ID: " + id);
        }
        cicloRepository.deleteById(id);
    }

    private Clube buscarClube(Integer idClube) {
        return clubeRepository.findById(idClube)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Clube não encontrado com ID: " + idClube));
    }
}
