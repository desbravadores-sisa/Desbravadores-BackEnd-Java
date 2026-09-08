package school.sptech.APIDesbravadores.service;

import org.springframework.stereotype.Service;
import school.sptech.APIDesbravadores.domain.Perfil;
import school.sptech.APIDesbravadores.repository.PerfilRepository;

import java.util.List;

@Service
public class PerfilService {

    private final PerfilRepository perfilRepository;

    public PerfilService(PerfilRepository perfilRepository) {
        this.perfilRepository = perfilRepository;
    }

    public List<Perfil> listarPerfis(){
        return perfilRepository.findAll();
    }
}
