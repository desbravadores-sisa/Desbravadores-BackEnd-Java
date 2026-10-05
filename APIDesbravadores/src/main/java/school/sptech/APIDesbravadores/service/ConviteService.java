package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.Convite;
import school.sptech.APIDesbravadores.domain.Perfil;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.dto.ConviteCriacaoRequestDto;
import school.sptech.APIDesbravadores.dto.ConviteRequestDto;
import school.sptech.APIDesbravadores.dto.ConviteResponseDto;
import school.sptech.APIDesbravadores.dto.ConviteUpdateDto;
import school.sptech.APIDesbravadores.exception.*;
import school.sptech.APIDesbravadores.mapper.ConviteMapper;
import school.sptech.APIDesbravadores.repository.ClubeRepository;
import school.sptech.APIDesbravadores.repository.ConviteRepository;
import school.sptech.APIDesbravadores.repository.PerfilRepository;
import school.sptech.APIDesbravadores.repository.UnidadeRepository;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;

import java.util.List;
import java.util.Optional;

@Service
public class ConviteService {

    private final ConviteRepository conviteRepository;
    private final ClubeRepository clubeRepository;
    private final UnidadeRepository unidadeRepository;
    private final PerfilRepository perfilRepository;
    private final EmailService emailService;
    private final AcessoService acesso;
    private final SecureRandom secureRandom = new SecureRandom();

    public ConviteService(ConviteRepository conviteRepository, ClubeRepository clubeRepository, UnidadeRepository unidadeRepository, PerfilRepository perfilRepository, EmailService emailService, AcessoService acesso) {
        this.conviteRepository = conviteRepository;
        this.clubeRepository = clubeRepository;
        this.unidadeRepository = unidadeRepository;
        this.perfilRepository = perfilRepository;
        this.emailService = emailService;
        this.acesso = acesso;
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

    public Convite validarConvite(String token){
        Convite convite = conviteRepository.findByToken(token).orElseThrow(ConviteNãoEncontradoException::new);
        String status = convite.getStatusConvite();

        validacaoClube(convite.getClube().getId());
        if (convite.getPerfil().getNome().equalsIgnoreCase("CONSELHEIRO")){
            validacaoUnidade(convite.getUnidade().getId());
        }

        if (convite.getDataExpiracao().isBefore(LocalDateTime.now()) && status.equalsIgnoreCase("pendente")){
            convite.setStatusConvite("EXPIRADO");
            status = "EXPIRADO";
            conviteRepository.save(convite);
        }
        if (status.equalsIgnoreCase("EXPIRADO")){
            throw new ConviteExpiradoException("Este convite já consta como expirado no sistema.");
        }
        if (status.equalsIgnoreCase("REVOGADO") || status.equalsIgnoreCase("ACEITO")){
            throw new ConviteConflitoEstadoException("Este convite já foi utilizado ou revogado pela diretoria.");
        }

        return convite;
    }


    public List<ConviteResponseDto> listarConvites(Integer idClube, String statusConvite){
        acesso.exigirDiretoria(); acesso.validarClube(idClube);
        validacaoClube(idClube);
        List<Convite> convites = new ArrayList<>();
        if (statusConvite != null && !statusConvite.isBlank()){
            convites = conviteRepository.findByClubeIdAndStatusConviteIgnoreCase(idClube,statusConvite);
        } else {
            convites = conviteRepository.findByClubeId(idClube);
        }
        return convites.stream()
                .map(ConviteMapper::toResponse)
                .toList();
    }

    @org.springframework.transaction.annotation.Transactional
    public Convite criarConvite(ConviteCriacaoRequestDto request, Integer idClubeLogado){
        acesso.exigirDiretoria(); acesso.validarClube(idClubeLogado);
        // Regra 1: O clube do usuário deve existir e ser o mesmo do usuário
        Clube clube = clubeRepository.buscarBloqueado(idClubeLogado).orElseThrow(ClubeNãoEncontradoException::new);

        Perfil perfil = perfilRepository.findById(request.getIdPerfil()).orElseThrow(PerfilNaoEncontradoException::new);
        if (!perfil.getNome().equalsIgnoreCase("CONSELHEIRO") && !AcessoService.perfilDiretoria(perfil.getNome())) {
            throw new RegraNegocioException("Perfil não permitido para convites.");
        }

        // Regra 2: Unidade deve existir e pertencer ao clube(se informada)
        Unidade unidade = null;

        if (request.getIdUnidade() != null){
            unidade = unidadeRepository.findById(request.getIdUnidade()).orElseThrow(() -> new UnidadeNãoEncontradaException());

            if (!unidade.getClube().getId().equals(clube.getId())){
                throw new AcessoNegadoException("Acesso negado: A unidade informada não pertence ao seu clube.");
            }
        } else if (perfil.getNome().equalsIgnoreCase("CONSELHEIRO")){
            throw new RegraNegocioException("Um conselheiro precisa obrigatoriamente estar vinculado a uma unidade.");
        }

        // Regra 3: Só pode haver um convite por e-mail ativo
        if (conviteRepository.existsByEmailAndStatusConvite(request.getEmail(),"PENDENTE")){
            throw new ConviteDuplicadoException("Já existe um convite pendente para este e-mail.");
        }

        // Geração do Convite
        Convite convite = new Convite();
        convite.setStatusConvite("PENDENTE");
        convite.setEmail(request.getEmail());
        convite.setToken(gerarTokenBase64());
        convite.setClube(clube);
        convite.setUnidade(unidade);
        convite.setPerfil(perfil);
        convite.setDataExpiracao(LocalDateTime.now().plusDays(14));

        conviteRepository.save(convite);
        emailService.sendInvitationEmail(convite);

        return convite;
    }

    public void excluirConvite(Integer idClube, Integer idConvite){
        acesso.exigirDiretoria(); acesso.validarClube(idClube);
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
