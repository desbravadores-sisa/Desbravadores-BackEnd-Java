package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(readOnly = true)
    public List<UnidadeResponseDto> listaUnidade(Integer idClube){
        if (!clubeRepository.existsById(idClube)){
            throw new ClubeNãoEncontradoException();
        }

        // Clube sem unidades não é erro: devolve lista vazia e o controller responde 204.
        return UnidadeMapper.toResponse(unidadeRepository.findByClubeId(idClube));
    }

    @Transactional(readOnly = true)
    public UnidadeResponseDto buscarUnidadePorId(Integer id){
        Unidade unidade = unidadeRepository.findById(id)
                .orElseThrow(UnidadeNãoEncontradaException::new);
        return UnidadeMapper.toResponse(unidade);
    }

    @Transactional
    public UnidadeResponseDto cadastrarUnidade(UnidadeCriacaoDto request, Integer idClube){
        Optional<Clube> clube = clubeRepository.findById(idClube);
        if (clube.isEmpty()){
            throw new ClubeNãoEncontradoException();
        }
        if (unidadeRepository.existsByClubeIdAndNome(idClube, request.getNome())){
            throw new UnidadeJácadastradaException();
        }
        Unidade unidade = UnidadeMapper.toEntity(request, clube.get());
        unidadeRepository.save(unidade);
        return UnidadeMapper.toResponse(unidade);
    }

    @Transactional
    public UnidadeResponseDto atualizarUnidade(UnidadeAtualizacaoDto request){
        // Carrega a unidade existente e altera só o que veio no DTO. Construir uma
        // entidade nova com o mesmo ID apagaria os campos ausentes na requisição.
        Unidade unidade = unidadeRepository.findById(request.getIdUnidade())
                .orElseThrow(UnidadeNãoEncontradaException::new);

        boolean nomeMudou = !unidade.getNome().equals(request.getNome());
        if (nomeMudou && unidadeRepository.existsByClubeIdAndNome(unidade.getClube().getId(), request.getNome())){
            throw new UnidadeJácadastradaException();
        }

        unidade.setNome(request.getNome());
        unidade.setGenero(request.getGenero());
        unidade.setIdadeMinima(request.getIdadeMinima());
        unidade.setIdadeMaxima(request.getIdadeMaxima());

        unidadeRepository.save(unidade);
        return UnidadeMapper.toResponse(unidade);
    }

    @Transactional
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
