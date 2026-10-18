package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.Convite;
import school.sptech.APIDesbravadores.domain.Perfil;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.dto.ConviteCriacaoDto;
import school.sptech.APIDesbravadores.dto.ConviteRequestDto;
import school.sptech.APIDesbravadores.dto.ConviteUpdateDto;
import school.sptech.APIDesbravadores.exception.*;
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

    public void validacaoClube(Integer idClube){
        if (!clubeRepository.existsById(idClube)){
            throw new ClubeNãoEncontradoException();
        }
    }

    public void validacaoUnidade(Integer idUnidade){
        if (!unidadeRepository.existsById(idUnidade)){
            throw new UnidadeNãoEncontradaException();
        }
    }

    public List<Convite> listarConvites(Integer idClube){
        if (!clubeRepository.existsById(idClube)){
            throw  new ClubeNãoEncontradoException();
        }
        List<Convite> convites = conviteRepository.findByClubeId(idClube);
        return convites;
    }

    public Convite criarConvite(ConviteCriacaoDto request, Integer idClube){
        if (!clubeRepository.existsById(idClube)){
            throw new ClubeNãoEncontradoException();
        }
        if (request.getIdUnidade() != null){
            if (!unidadeRepository.existsById(request.getIdUnidade())){
                throw new UnidadeNãoEncontradaException();
            }
        }
        Perfil perfil = perfilRepository.findById(request.getIdPerfil())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Perfil não encontrado"));
        Convite convite = new Convite();
        convite.setEmail(request.getEmail());
        convite.setPerfil(perfil);
        convite.setToken(gerarTokenBase64());
        convite.setStatusConvite(STATUS_PENDENTE);
        convite.setDataExpiracao(LocalDateTime.now().plusDays(7));
        Optional<Clube> clube = clubeRepository.findById(idClube);
        convite.setClube(clube.get());
        if (request.getIdUnidade() != null){
            Optional<Unidade> unidade = unidadeRepository.findById(request.getIdUnidade());
            unidade.ifPresent(convite::setUnidade);
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

    public void excluirConvite(Integer idClube, Integer idConvite){
        validacaoClube(idClube);
        Convite convite = conviteRepository.findById(idConvite).orElseThrow(ConviteNãoEncontradoException::new);
        if (!idClube.equals(convite.getClube().getId())){
            throw new AcessoNegadoException("Ação não permitida: O convite informado pertence a outro clube.");
        }
        conviteRepository.delete(convite);
    }

    private String gerarTokenBase64() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
