package AfetYonetimSistemi.DTO;

import AfetYonetimSistemi.Model.Oncelik;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public class KaynakTalepResponse {

    private Long kaynakTalepId;

    private Oncelik oncelik;

    private String acıklama;

    private LocalDateTime olusturulmaTarihi;

    private List<KaynakTalepKalemResponse> kalemler;
}
