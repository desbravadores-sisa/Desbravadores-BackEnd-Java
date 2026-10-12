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
    private final school.sptech.APIDesbravadores.repository.CadernoRepository cadernoRepository;

    public CicloService(CicloRepository cicloRepository, ClubeRepository clubeRepository) {
        this(cicloRepository, clubeRepository, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public CicloService(CicloRepository cicloRepository, ClubeRepository clubeRepository,
                        school.sptech.APIDesbravadores.repository.CadernoRepository cadernoRepository) {
        this.cicloRepository = cicloRepository;
        this.clubeRepository = clubeRepository;
        this.cadernoRepository = cadernoRepository;
    }

    /**
     * "Novo ciclo": o ciclo criado como ativo (padrão) desativa o anterior do clube e passa a
     * ser o ciclo de todos os cadernos. O ranking (vw_resumo_unidades) usa só o ciclo ativo,
     * então recomeça do zero; tarefas, pontos e descontos do ciclo anterior ficam guardados.
     */
    @Transactional
    public CicloResponseDto create(CicloCriacaoDto dto, Integer idClubeUsuario) {
        if (idClubeUsuario != null) {
            dto.setIdClube(idClubeUsuario);
        }
        if (dto.getIdClube() == null) {
            throw new school.sptech.APIDesbravadores.exception.RequisicaoInvalidaException("Clube não informado");
        }
        if (dto.getAtivo() == null) {
            dto.setAtivo(true);
        }
        CicloResponseDto criado = create(dto);
        if (Boolean.TRUE.equals(dto.getAtivo())) {
            tornarAtivo(criado.getId());
        }
        return criado;
    }

    private void tornarAtivo(Integer idCiclo) {
        Ciclo ciclo = cicloRepository.findById(idCiclo).orElseThrow();
        Integer idClube = ciclo.getClube().getId();
        for (Ciclo outro : cicloRepository.findByClubeId(idClube)) {
            if (!outro.getId().equals(idCiclo) && Boolean.TRUE.equals(outro.getAtivo())) {
                outro.setAtivo(false);
                cicloRepository.save(outro);
            }
        }
        if (cadernoRepository != null) {
            cadernoRepository.findByClubeId(idClube).forEach(c -> {
                c.setCiclo(ciclo);
                cadernoRepository.save(c);
            });
        }
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
    public CicloResponseDto update(Integer id, CicloAtualizacaoDto dto, Integer idClubeUsuario) {
        if (idClubeUsuario != null) {
            dto.setIdClube(idClubeUsuario);
        }
        CicloResponseDto atualizado = update(id, dto);
        if (Boolean.TRUE.equals(dto.getAtivo())) {
            tornarAtivo(id);
        }
        return atualizado;
    }

    @Transactional
    public CicloResponseDto update(Integer id, CicloAtualizacaoDto dto) {
        Ciclo ciclo = cicloRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Ciclo não encontrado com ID: " + id));
        if (dto.getIdClube() != null && ciclo.getClube() != null && !dto.getIdClube().equals(ciclo.getClube().getId())) {
            throw new EntidadeNaoEncontradaException("Ciclo não encontrado com ID: " + id);
        }

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
