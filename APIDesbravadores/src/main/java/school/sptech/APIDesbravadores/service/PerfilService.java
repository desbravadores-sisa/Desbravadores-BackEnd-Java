package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.sptech.APIDesbravadores.domain.Perfil;
import school.sptech.APIDesbravadores.dto.PerfilAtualizacaoDto;
import school.sptech.APIDesbravadores.dto.PerfilCriacaoDto;
import school.sptech.APIDesbravadores.dto.PerfilResponseDto;
import school.sptech.APIDesbravadores.exception.EntidadeNaoEncontradaException;
import school.sptech.APIDesbravadores.mapper.PerfilMapper;
import school.sptech.APIDesbravadores.repository.PerfilRepository;

import java.util.List;

@Service
public class PerfilService {

    private final PerfilRepository perfilRepository;

    public PerfilService(PerfilRepository perfilRepository) {
        this.perfilRepository = perfilRepository;
    }

    @Transactional
    public PerfilResponseDto create(PerfilCriacaoDto dto) {
        Perfil perfil = PerfilMapper.toEntity(dto);
        Perfil saved = perfilRepository.save(perfil);
        return PerfilMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PerfilResponseDto> findAll() {
        return PerfilMapper.toResponse(perfilRepository.findAll());
    }

    @Transactional(readOnly = true)
    public PerfilResponseDto findById(Integer id) {
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Perfil não encontrado com ID: " + id));
        return PerfilMapper.toResponse(perfil);
    }

    @Transactional
    public PerfilResponseDto update(Integer id, PerfilAtualizacaoDto dto) {
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Perfil não encontrado com ID: " + id));
        PerfilMapper.updateEntity(dto, perfil);
        Perfil saved = perfilRepository.save(perfil);
        return PerfilMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Integer id) {
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Perfil não encontrado com ID: " + id));
        perfilRepository.delete(perfil);
    }
}
