package AfetYonetimSistemi.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AfetAktifleştirmeRequest {

    @NotBlank(message = "Afet ili belirtilmelidir")
    @Size(min = 3 , max = 20 , message = "İl 3 ila 20 karekterden olusmalıdır")
    private String il;

    @NotNull(message = "Afet ID belirtilmelidir")
    @Positive(message = "Gecersiz afet ID girdiniz")
    private Long afetId;

    @NotNull(message = "latitude alanı bos olamaz")
    @DecimalMin(value = "35.0", message = "koordinatlar turkıye sınırları dısındadır")
    @DecimalMax(value = "43.0", message = "koordinatlar turkıye sınırları dısındadır")
    private double lat;

    @NotNull(message = "longitude alanı bos olamaz")
    @DecimalMin(value = "25.0", message = "koordinatlar turkıye sınırları dısındadır")
    @DecimalMax(value = "45.0", message = "koordinatlar turkıye sınırları dısındadır")
    private double lon;
}
