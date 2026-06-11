package AfetYonetimSistemi.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class KoordinatorOlusturmaRequest {
    @Size(min = 11 , max = 11 , message = "Tc 11 haneli olmalıdır")
    @NotBlank(message = "tc bos olamaz")
    private String tc;

    @Pattern(
            regexp = "^05\\d{9}$",
            message = "Geçerli bir telefon numarası giriniz (05xxxxxxxxx)"
    )
    private String telNo;

    @NotBlank(message = "email bos olamaz")
    @Email(message = "email adresi email formatında olmalıdır")
    private String email;

    @Size(min = 3 , max = 30 , message = "Kullanıcı ısmı cok kısa veya cok uzun")
    @NotBlank(message = "Kullanıcı ismi bos olamaz")
    private String username;

    @NotBlank(message = "İl kısmı bos olamaz")
    @Size(min = 3 , max = 20 , message = "İl ismi uzunlugu max 20 olabılır")
    private String gorevYaptıgıIl;

}
