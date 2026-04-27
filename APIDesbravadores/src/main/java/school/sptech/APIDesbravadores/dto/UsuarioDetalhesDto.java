package school.sptech.APIDesbravadores.dto;

import lombok.ToString;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import school.sptech.APIDesbravadores.domain.Usuario;

import java.util.Collection;
import java.util.List;

@ToString
public class UsuarioDetalhesDto implements UserDetails {

    private final String nome;

    private final String email;

    private final String senha;

    private final String tipoConta;

    private Integer idClube;

    private Integer idUnidade;

    public UsuarioDetalhesDto(String nome, String email, String senha, String tipoConta) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipoConta = tipoConta;
    }

    public UsuarioDetalhesDto(Usuario usuario) {
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.senha = usuario.getSenha();
        this.tipoConta = usuario.getTipoConta();

        if (usuario.getClube().getId() != null){
            this.idClube = usuario.getClube().getId();
        }

        if (usuario.getUnidade() != null && usuario.getUnidade().getId() != null){
            this.idUnidade = usuario.getUnidade().getId();
        }
    }

    public Integer getIdClube() {
        return idClube;
    }

    public Integer getIdUnidade() {
        return idUnidade;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getTipoConta() {
        return tipoConta;
    }

    @Override
    public boolean isAccountNonExpired() {return true;}

    @Override
    public boolean isAccountNonLocked() {return true;}

    @Override
    public boolean isCredentialsNonExpired() {return true;}

    @Override
    public boolean isEnabled() {return true;}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.tipoConta.toUpperCase()));
    }

    @Override
    public @Nullable String getPassword() {return this.senha;}

    @Override
    public String getUsername() {return this.email;}
}
