package library_management.com.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class KodGonderRequest {
    @NotBlank(message = "TC bos olamaz")
    @Size(min = 11, max = 11 , message = "TC 11 haneli olmalıdır")
    private String tc;

    @Pattern(
            regexp = "^05\\d{9}$",
            message = "Geçerli bir telefon numarası giriniz (05xxxxxxxxx)"
    )
    private String telNo;

    @NotBlank(message = "Kod bos olamaz")
    @Size(max = 6 , message = "Kod 6 karekterden olusmalıdır")
    private String kod;
}
