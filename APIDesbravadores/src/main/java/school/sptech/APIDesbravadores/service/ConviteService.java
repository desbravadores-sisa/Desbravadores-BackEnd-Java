package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.Convite;
import school.sptech.APIDesbravadores.domain.Perfil;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.dto.ConviteRequestDto;
import school.sptech.APIDesbravadores.dto.ConviteUpdateDto;
import school.sptech.APIDesbravadores.exception.ClubeNãoEncontradoException;
import school.sptech.APIDesbravadores.exception.ConviteNãoEncontradoException;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.exception.UnidadeNãoEncontradaException;
import school.sptech.APIDesbravadores.mapper.ConviteMapper;
import school.sptech.APIDesbravadores.repository.ClubeRepository;
import school.sptech.APIDesbravadores.repository.ConviteRepository;
import school.sptech.APIDesbravadores.repository.PerfilRepository;
import school.sptech.APIDesbravadores.repository.UnidadeRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import java.util.List;
import java.util.Optional;

@Service
public class ConviteService {

    private static final String STATUS_PENDENTE = "PENDENTE";
    private static final String STATUS_ACEITO = "ACEITO";
    private static final String STATUS_REVOGADO = "REVOGADO";
    private static final String STATUS_EXPIRADO = "EXPIRADO";

    private final ConviteRepository conviteRepository;
    private final ClubeRepository clubeRepository;
    private final UnidadeRepository unidadeRepository;
    private final PerfilRepository perfilRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public ConviteService(ConviteRepository conviteRepository, ClubeRepository clubeRepository, UnidadeRepository unidadeRepository, PerfilRepository perfilRepository) {
        this.conviteRepository = conviteRepository;
        this.clubeRepository = clubeRepository;
        this.unidadeRepository = unidadeRepository;
        this.perfilRepository = perfilRepository;
    }

    public List<Convite> listarConvites(Integer idClube){
        System.out.println("IdClube na Service:" + idClube);
        if (!clubeRepository.existsById(idClube)){
            throw  new ClubeNãoEncontradoException();
        }
        List<Convite> convites = conviteRepository.findByClubeId(idClube);
        System.out.println(convites);
        return convites;
    }

    public Convite criarConvite(ConviteRequestDto request){
        if (!clubeRepository.existsById(request.getIdClube())){
            throw new ClubeNãoEncontradoException();
        }
        if (request.getIdUnidade() != null){
            if (!unidadeRepository.existsById(request.getIdUnidade())){
                throw new UnidadeNãoEncontradaException();
            }
        }
        Perfil perfil = perfilRepository.findByNomeIgnoreCase(request.getTipoConta())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Perfil não encontrado: " + request.getTipoConta()));
        Convite convite = ConviteMapper.toEntity(request, perfil);
        convite.setToken(gerarTokenBase64());
        Optional<Clube> clube = clubeRepository.findById(request.getIdClube());
        convite.setClube(clube.get());
        System.out.println("Após o settar o clube");
        System.out.println("Como está o IdUnidade:" + request.getIdUnidade());
        if (request.getIdUnidade() != null){
            Optional<Unidade> unidade = unidadeRepository.findById(request.getIdUnidade());
            if (!unidade.isEmpty()){
                convite.setUnidade(unidade.get());
            }
        }
        conviteRepository.save(convite);
        return convite;
    }

    public Boolean validarConvite(Integer idConvite){
        Optional<Convite> conviteValidation = conviteRepository.findById(idConvite);
        if (conviteValidation.isEmpty()){
            throw new ConviteNãoEncontradoException();
        }
        Convite convite = conviteValidation.get();
        if (convite.getDataExpiracao().isBefore(LocalDateTime.now()) && !STATUS_ACEITO.equalsIgnoreCase(convite.getStatusConvite())){
            convite.setStatusConvite(STATUS_EXPIRADO);
            conviteRepository.save(convite);
        }
        if (STATUS_EXPIRADO.equalsIgnoreCase(convite.getStatusConvite())
                || STATUS_REVOGADO.equalsIgnoreCase(convite.getStatusConvite())
                || STATUS_ACEITO.equalsIgnoreCase(convite.getStatusConvite())){
            return false;
        }
        return true;
    }

    public Convite atualizarConvite(ConviteUpdateDto updateDto, Integer idConvite){
        Optional<Convite> conviteValidation = conviteRepository.findById(idConvite);
        if (conviteValidation.isEmpty()){
            throw new ConviteNãoEncontradoException();
        }
        Convite convite = conviteValidation.get();
        if (updateDto.getStatusConvite() != null){
            convite.setStatusConvite(updateDto.getStatusConvite());
        }
        if (updateDto.getDataExpiracao() != null){
            convite.setDataExpiracao(updateDto.getDataExpiracao());
        }
        conviteRepository.save(convite);
        return convite;
    }

    private String gerarTokenBase64() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
