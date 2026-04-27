package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import school.sptech.APIDesbravadores.domain.Clube;
import school.sptech.APIDesbravadores.domain.Unidade;
import school.sptech.APIDesbravadores.domain.Usuario;
import school.sptech.APIDesbravadores.dto.UnidadeAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.UnidadeCriacaoDto;
import school.sptech.APIDesbravadores.dto.UnidadeResponseDto;
import school.sptech.APIDesbravadores.exception.ClubeNãoEncontradoException;
import school.sptech.APIDesbravadores.exception.UnidadeJácadastradaException;
import school.sptech.APIDesbravadores.exception.UnidadeNãoEncontradaException;
import school.sptech.APIDesbravadores.mapper.UnidadeMapper;
import school.sptech.APIDesbravadores.repository.ClubeRepository;
import school.sptech.APIDesbravadores.repository.UnidadeRepository;
import school.sptech.APIDesbravadores.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;
    private final ClubeRepository clubeRepository;
    private final UsuarioRepository usuarioRepository;

    public UnidadeService(UnidadeRepository unidadeRepository, ClubeRepository clubeRepository, UsuarioRepository usuarioRepository) {
        this.unidadeRepository = unidadeRepository;
        this.clubeRepository = clubeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<UnidadeResponseDto> listaUnidade(Integer idClube){
        if (!clubeRepository.existsById(idClube)){
            throw new ClubeNãoEncontradoException();
        }

        List<Unidade> unidades = unidadeRepository.findByClubeId(idClube);

        if (unidades.isEmpty()){
            throw new UnidadeNãoEncontradaException();
        }

        return UnidadeMapper.toResponse(unidades);
    }

    public UnidadeResponseDto buscarUnidadePorId(Integer id){
        if (!unidadeRepository.existsById(id)){
            throw new UnidadeNãoEncontradaException();
        }
        Optional<Unidade> unidade = unidadeRepository.findById(id);
        System.out.println(unidade.get());
        return UnidadeMapper.toResponse(unidade.get());
    }

    public UnidadeResponseDto cadastrarUnidade(UnidadeCriacaoDto request, Integer idClube){
        Optional<Clube> clube = clubeRepository.findById(idClube);
        if (clube.isEmpty()){
            throw new ClubeNãoEncontradoException();
        }
        if (unidadeRepository.existsByClubeIdAndNome(idClube, request.getNome())){
            throw new UnidadeJácadastradaException();
        }
        Unidade unidade = UnidadeMapper.toEntity(request,clube.get());
        unidadeRepository.save(unidade);
        return UnidadeMapper.toResponse(unidade);
    }

    public UnidadeResponseDto atualizarUnidade(UnidadeAtualizacaoDto request){
        Optional<Unidade> unidade = unidadeRepository.findById(request.getIdUnidade());
        if (unidade.isEmpty()){
            throw new UnidadeNãoEncontradaException();
        }

        if (unidadeRepository.existsByClubeIdAndNome(unidade.get().getClube().getId(),request.getNome()) && (request.getPontuacao() == null || request.getPontuacao().equals(unidade.get().getPontuacao())) ){
            throw new UnidadeJácadastradaException();
        }
        Unidade unidadeReplace = UnidadeMapper.toEntity(request);
        Optional<Clube> clube = clubeRepository.findById(unidade.get().getClube().getId());
        unidadeReplace.setClube(clube.get());
        if (unidadeReplace.getPontuacao() == null){
            unidadeReplace.setPontuacao(unidade.get().getPontuacao());
        }
        unidadeRepository.save(unidadeReplace);
        return UnidadeMapper.toResponse(unidadeReplace);
    }

    public void deletarUnidade(Integer idUnidade){
        if (!unidadeRepository.existsById(idUnidade)){
            throw new UnidadeNãoEncontradaException();
        }
        List<Usuario> usuarios = usuarioRepository.findByUnidadeId(idUnidade);
        for (Usuario usuario : usuarios) {
            usuario.setUnidade(null);
            usuarioRepository.save(usuario);
        }
        unidadeRepository.deleteById(idUnidade);
    }
}
