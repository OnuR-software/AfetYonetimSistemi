package AfetYonetimSistemi.DTO;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserTalepKalemDTO {

    @Size(min = 1 , message = "malzeme id degerını minimum 1 olabılır")
    private List<Long> malzemeId;

    @Size(min = 1 , max = 10 , message = "kişi sayısı 1 ile 10 arası olmalıdır")
    private Integer kisiSayısı;

    private PinRequestDTO pinRequestDTO;

}
