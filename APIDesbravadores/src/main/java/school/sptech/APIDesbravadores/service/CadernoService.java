package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import school.sptech.APIDesbravadores.domain.ResumoCadernoView;
import school.sptech.APIDesbravadores.dto.CadernoDetalhesDto;
import school.sptech.APIDesbravadores.dto.GrupoCadernoDto;
import school.sptech.APIDesbravadores.exception.ClubeNãoEncontradoException;
import school.sptech.APIDesbravadores.repository.ClubeRepository;
import school.sptech.APIDesbravadores.repository.ResumoCadernoViewRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CadernoService {

    private final ResumoCadernoViewRepository resumoCadernoViewRepository;
    private final ClubeRepository clubeRepository;

    public CadernoService(ResumoCadernoViewRepository resumoCadernoViewRepository, ClubeRepository clubeRepository) {
        this.resumoCadernoViewRepository = resumoCadernoViewRepository;
        this.clubeRepository = clubeRepository;
    }

    public List<GrupoCadernoDto> listarResumosCadernos(Integer idClube){
        if (!clubeRepository.existsById(idClube)){
            throw new ClubeNãoEncontradoException();
        }
        List<ResumoCadernoView> view = resumoCadernoViewRepository.findByIdClubeOrderByIdadeAlvoAsc(idClube);
        Map<String, List<ResumoCadernoView>> cadernosAgrupados = view.stream()
                .collect(Collectors.groupingBy(ResumoCadernoView::getNomeGrupo));
        return cadernosAgrupados.entrySet().stream()
                .map(entry -> {
                    String nomeGrupo = entry.getKey();
                    List<CadernoDetalhesDto> detalhes = entry.getValue().stream()
                            .map(v -> new CadernoDetalhesDto( // Usando o construtor da nossa classe
                                    v.getIdCaderno(),
                                    v.getNomeCaderno(),
                                    v.getIdadeAlvo(),
                                    v.getTotalVinculados(),
                                    v.getTotalRequisitos()
                            ))
                            .toList();

                    return new GrupoCadernoDto(nomeGrupo, detalhes);
                })
                .toList();
    }
}
