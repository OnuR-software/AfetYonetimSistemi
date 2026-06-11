package AfetYonetimSistemi.DTO;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class KaynakTalepIptalRequest {

    @Size(max = 100 , message = "Aciklama 100 karakterden fazla olamaz")
    private String aciklama;
}
