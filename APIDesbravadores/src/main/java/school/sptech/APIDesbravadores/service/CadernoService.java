package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.Caderno;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.ResumoCadernoView;
import school.sptech.APIDesbravadores.dto.CadernoAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.CadernoCriacaoDto;
import school.sptech.APIDesbravadores.dto.CadernoDetalhesDto;
import school.sptech.APIDesbravadores.dto.CadernoResponseDto;
import school.sptech.APIDesbravadores.dto.GrupoCadernoDto;
import school.sptech.APIDesbravadores.exception.ClubeNãoEncontradoException;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.mapper.CadernoMapper;
import school.sptech.APIDesbravadores.repository.CadernoRepository;
import school.sptech.APIDesbravadores.repository.ClubeRepository;
import school.sptech.APIDesbravadores.repository.ResumoCadernoViewRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CadernoService {

    private final CadernoRepository cadernoRepository;
    private final ClubeRepository clubeRepository;
    private final ResumoCadernoViewRepository resumoCadernoViewRepository;

    public CadernoService(CadernoRepository cadernoRepository, ClubeRepository clubeRepository, ResumoCadernoViewRepository resumoCadernoViewRepository) {
        this.cadernoRepository = cadernoRepository;
        this.clubeRepository = clubeRepository;
        this.resumoCadernoViewRepository = resumoCadernoViewRepository;
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
                            .map(v -> new CadernoDetalhesDto(
                                    v.getIdCaderno(),
                                    v.getNomeCaderno(),
                                    v.getIdadeAlvo(),
                                    v.getTotalVinculados(),
                                    v.getTotalRequisitos(),
                                    v.getDataInicio(),
                                    v.getDataFim(),
                                    v.getStatusCaderno()
                            ))
                            .toList();

                    return new GrupoCadernoDto(nomeGrupo, detalhes);
                })
                .toList();
    }

    private Clube buscarClube(Integer idClube) {
        return clubeRepository.findById(idClube)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Clube não encontrado com ID: " + idClube));
    }
}
