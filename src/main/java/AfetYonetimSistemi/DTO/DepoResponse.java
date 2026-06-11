package AfetYonetimSistemi.DTO;

import AfetYonetimSistemi.Model.DepoModeli;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DepoResponse {

    private Long depoId;

    private String depoAdi;

    private DepoModeli depoModeli;

    private List<MalzemeResponseDTO> malzemeler;
}
