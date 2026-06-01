package library_management.com.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "TC bos olamaz")
    @Size(min = 11, max = 11 , message = "TC 11 haneli olmalıdır")
    private String tc;

    @NotBlank(message = "Sifre bos olamaz")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&.])[ A-Za-z\\d@$!%*?&.]{8,16}$",
            message = "Şifre en az 8 en fazla 16 karakter, bir büyük harf, bir küçük harf, bir rakam ve bir özel karakter içermelidir"
    )
    private String password;

    @Pattern(
            regexp = "^05\\d{9}$",
            message = "Geçerli bir telefon numarası giriniz (05xxxxxxxxx)"
    )
    private String telNo;
}
