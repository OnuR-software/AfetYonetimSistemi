package AfetYonetimSistemi.DTO;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DepoTalepMalzeme {

    private String malzemeAdi;

    private Integer miktar;
}
