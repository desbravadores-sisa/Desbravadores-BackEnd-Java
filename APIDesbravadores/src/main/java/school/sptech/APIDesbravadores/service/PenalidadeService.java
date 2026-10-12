package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.Ciclo;
import school.sptech.APIDesbravadores.domain.Penalidade;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.domain.Usuario;
import school.sptech.APIDesbravadores.dto.PenalidadeCriacaoDto;
import school.sptech.APIDesbravadores.dto.PenalidadeDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.exception.RequisicaoInvalidaException;
import school.sptech.APIDesbravadores.repository.CicloRepository;
import school.sptech.APIDesbravadores.repository.PenalidadeRepository;
import school.sptech.APIDesbravadores.repository.UnidadeRepository;
import school.sptech.APIDesbravadores.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/** Descontos de pontos (penalidades) lançados pela Diretoria, sempre no ciclo ativo do clube. */
@Service
public class PenalidadeService {

    public static final String TIPO_NOTIFICACAO = "PENALIDADE";

    private final PenalidadeRepository penalidadeRepository;
    private final UnidadeRepository unidadeRepository;
    private final CicloRepository cicloRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacaoService notificacaoService;

    public PenalidadeService(PenalidadeRepository penalidadeRepository, UnidadeRepository unidadeRepository,
                             CicloRepository cicloRepository, UsuarioRepository usuarioRepository,
                             NotificacaoService notificacaoService) {
        this.penalidadeRepository = penalidadeRepository;
        this.unidadeRepository = unidadeRepository;
        this.cicloRepository = cicloRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacaoService = notificacaoService;
    }

    @Transactional(readOnly = true)
    public List<PenalidadeDto> listarDoCicloAtivo(Integer idUnidade, Integer idClube) {
        unidadeDoClube(idUnidade, idClube);
        return cicloRepository.findByClubeIdAndAtivoTrue(idClube)
                .map(c -> penalidadeRepository.findByUnidadeIdAndCicloIdOrderByDataRegistroDesc(idUnidade, c.getId()))
                .orElse(List.of())
                .stream().map(PenalidadeService::toDto).toList();
    }

    @Transactional
    public PenalidadeDto lancar(Integer idUnidade, PenalidadeCriacaoDto dto, Integer idClube, String emailAutor) {
        Unidade unidade = unidadeDoClube(idUnidade, idClube);
        Ciclo ciclo = cicloRepository.findByClubeIdAndAtivoTrue(idClube)
                .orElseThrow(() -> new RequisicaoInvalidaException("Não há ciclo ativo para lançar o desconto"));
        Usuario autor = emailAutor == null ? null : usuarioRepository.findByEmail(emailAutor).orElse(null);

        Penalidade p = new Penalidade();
        p.setUnidade(unidade);
        p.setCiclo(ciclo);
        p.setUsuario(autor);
        p.setPontos(dto.pontos());
        p.setMotivo(dto.motivo().trim());
        p.setDataRegistro(LocalDateTime.now());
        penalidadeRepository.save(p);

        notificacaoService.notificarUnidade(unidade, "Desconto de pontos",
                "A Diretoria descontou " + dto.pontos() + " pontos da unidade: " + p.getMotivo(),
                TIPO_NOTIFICACAO, p.getId());
        return toDto(p);
    }

    @Transactional
    public void remover(Integer idPenalidade, Integer idClube) {
        Penalidade p = penalidadeRepository.findById(idPenalidade)
                .filter(x -> x.getUnidade() != null && x.getUnidade().getClube() != null
                        && Objects.equals(x.getUnidade().getClube().getId(), idClube))
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Desconto não encontrado no seu clube"));
        penalidadeRepository.delete(p);
    }

    private Unidade unidadeDoClube(Integer idUnidade, Integer idClube) {
        return unidadeRepository.findById(idUnidade)
                .filter(u -> u.getClube() != null && Objects.equals(u.getClube().getId(), idClube))
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Unidade não encontrada no seu clube"));
    }

    static PenalidadeDto toDto(Penalidade p) {
        return new PenalidadeDto(p.getId(), p.getUnidade().getId(), p.getCiclo().getId(), p.getPontos(), p.getMotivo(),
                p.getDataRegistro(), p.getUsuario() != null ? p.getUsuario().getNome() : null);
    }
}
