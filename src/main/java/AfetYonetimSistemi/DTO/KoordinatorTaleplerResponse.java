package AfetYonetimSistemi.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder

public class KoordinatorTaleplerResponse {

    private Long talepId;

    private Integer kisiSayisi;

    private List<String> malzemeler;

    private PinResponseDTO pinResponseDTO;

}
