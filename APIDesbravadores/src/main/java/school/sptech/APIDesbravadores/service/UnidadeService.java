package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.dto.UnidadeResponseDto;
import school.sptech.APIDesbravadores.exception.ClubeNãoEncontradoException;
import school.sptech.APIDesbravadores.exception.UnidadesNãoCadastradasException;
import school.sptech.APIDesbravadores.mapper.UnidadeMapper;
import school.sptech.APIDesbravadores.repository.ClubeRepository;
import school.sptech.APIDesbravadores.repository.UnidadeRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;
    private final ClubeRepository clubeRepository;

    public UnidadeService(UnidadeRepository unidadeRepository, ClubeRepository clubeRepository) {
        this.unidadeRepository = unidadeRepository;
        this.clubeRepository = clubeRepository;
    }

    public List<UnidadeResponseDto> listaUnidade(Integer idClube){
        if (!clubeRepository.existsById(idClube)){
            throw new ClubeNãoEncontradoException();
        }

        List<Unidade> unidades = unidadeRepository.findByClubeId(idClube);

        if (unidades.isEmpty()){
            throw new UnidadesNãoCadastradasException();
        }

        return UnidadeMapper.toResponse(unidades);
    }
}
