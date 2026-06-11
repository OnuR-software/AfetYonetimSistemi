package AfetYonetimSistemi.DTO;

import AfetYonetimSistemi.Model.MalzemeDurumu;
import AfetYonetimSistemi.Model.MalzemeKategori;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MalzemeResponseDTO {

    private Long id;
    private String malzemeAdi;
    private MalzemeKategori malzemeKategori;
    private Integer stok;
    private MalzemeDurumu malzemeDurumu;
}
