package library_management.com.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import library_management.com.Model.AfetTuru;
import lombok.Data;

@Data
public class AfetAktifleştirmeRequest {

    @NotBlank(message = "Afet ili belirtilmelidir")
    @Size(min = 3 , max = 20 , message = "İl 3 ila 20 karekterden olusmalıdır")
    private String il;

    @NotBlank(message = "Afet turu belirtilmelidir")
    @Size(min = 1 , max = 5 , message = "Gecersiz afet ıd girdiniz")
    private Long afetId;
}
