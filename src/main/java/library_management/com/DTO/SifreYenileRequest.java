package library_management.com.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SifreYenileRequest {

    @NotBlank(message = "Sifre bos olamaz")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&.])[ A-Za-z\\d@$!%*?&.]{8,16}$",
            message = "Şifre en az 8 en fazla 16 karakter, bir büyük harf, bir küçük harf, bir rakam ve bir özel karakter içermelidir"
    )
    private String sifre;

}
