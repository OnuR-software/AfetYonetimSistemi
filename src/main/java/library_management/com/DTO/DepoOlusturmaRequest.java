package library_management.com.DTO;

import jakarta.validation.constraints.*;
import library_management.com.Model.DepoModeli;
import lombok.Data;

@Data
public class DepoOlusturmaRequest {

    @NotBlank(message = "Depo adı boş olamaz")
    @Size(max = 30 , message = "Depo adı maksımum 30 karekterlı olabılır")
    private String depoAdi;

    @NotBlank(message = "Depo modeli belirtilmelidir")
    private DepoModeli depoModeli;

    @NotBlank(message = "Deponun hangı ilde oldugu belırtılmelıdır")
    @Size(min = 3 , max = 20 , message = "Il maksimum 20 karekterlı olmalıdır")
    private String il;

    @NotNull(message = "latitude alanı bos olamaz")
    @DecimalMin(value = "35.0", message = "koordinatlar turkıye sınırları dısındadır")
    @DecimalMax(value = "43.0", message = "koordinatlar turkıye sınırları dısındadır")
    private Double latitude;

    @NotNull(message = "longitude alanı bos olamaz")
    @DecimalMin(value = "25.0", message = "koordinatlar turkıye sınırları dısındadır")
    @DecimalMax(value = "45.0", message = "koordinatlar turkıye sınırları dısındadır")
    private Double longitude;
}
