package school.sptech.APIDesbravadores.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioSenhaUpdateDto {

    @NotBlank(message = "Informe a senha atual.")
    private String senhaAtual;

    @NotBlank(message = "Informe a nova senha.")
    @Size(min = 6, message = "A nova senha deve ter ao menos 6 caracteres.")
    private String novaSenha;

    @NotBlank(message = "Confirme a nova senha.")
    private String confirmacaoNovaSenha;

    @AssertTrue(message = "A confirmação deve ser igual à nova senha.")
    public boolean isConfirmacaoNovaSenhaValida() {
        return novaSenha == null || novaSenha.isBlank()
                || confirmacaoNovaSenha == null || confirmacaoNovaSenha.isBlank()
                || novaSenha.equals(confirmacaoNovaSenha);
    }
}
