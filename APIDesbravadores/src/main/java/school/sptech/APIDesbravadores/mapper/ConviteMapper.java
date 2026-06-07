package school.sptech.APIDesbravadores.mapper;

import school.sptech.APIDesbravadores.domain.Convite;
import school.sptech.APIDesbravadores.dto.ConviteRequestDto;
import school.sptech.APIDesbravadores.dto.ConviteResponseDto;

public class ConviteMapper {

    public static ConviteResponseDto toResponse(Convite convite){
        if (convite == null){
            return null;
        }
        ConviteResponseDto dto = new ConviteResponseDto();
        dto.setId(convite.getId());
        dto.setEmail(convite.getEmail());
        dto.setDataExpiracao(convite.getDataExpiracao());
        dto.setTipoConta(convite.getTipoConta());
        dto.setStatusConvite(convite.getStatusConvite());
        if (convite.getUnidade() != null){
            dto.setNomeUnidade(convite.getUnidade().getNome());
        }
        return dto;
    }

    public static Convite toEntity(ConviteRequestDto requestDto){
        if (requestDto == null){
            return null;
        }
        Convite convite = new Convite();
        convite.setEmail(requestDto.getEmail());
        convite.setStatusConvite("pendente");
        convite.setTipoConta(requestDto.getTipoConta());
        convite.setDataExpiracao(requestDto.getDataExpiracao());
        return convite;
    }
}
