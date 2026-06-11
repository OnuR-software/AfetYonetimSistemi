package AfetYonetimSistemi.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import AfetYonetimSistemi.Model.KaynakTuru;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StokRequestDTO {

    @Size(min = 1 , max = 10000 , message = "Miktar minimum 1 olmalıdır")
    private Integer miktar;

    @NotBlank(message = "KaynakTuru secilmek zorundadır")
    private KaynakTuru kaynakTuru;

    @Size(min = 1 , message = "MalzemeId girilmek zorundadır")
    private Long malzemeId;

    @Size(min = 8 , max = 30 , message = "Bagıscı adı 8-30 karekterden olusmalıdır")
    private String bagıscıAdı;
}
