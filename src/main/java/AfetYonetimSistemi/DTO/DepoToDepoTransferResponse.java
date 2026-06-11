package AfetYonetimSistemi.DTO;

import AfetYonetimSistemi.Model.Oncelik;
import AfetYonetimSistemi.Model.TransferDurumu;
import lombok.Builder;

import java.util.List;

@Builder
public class DepoToDepoTransferResponse {

    private Long talepId;

    private Oncelik oncelik;

    private TransferDurumu transferDurumu;

    private double latitude;

    private double longitude;

    private String adminNotu;

    private List<DepoTalepMalzeme> malzemeler;
}
