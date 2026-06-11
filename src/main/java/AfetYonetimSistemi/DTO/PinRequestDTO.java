package AfetYonetimSistemi.DTO;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import AfetYonetimSistemi.Model.PinTuru;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PinRequestDTO {

    @NotNull(message = "latitude alanı bos olamaz")
    @DecimalMin(value = "35.0", message = "koordinatlar turkıye sınırları dısındadır")
    @DecimalMax(value = "43.0", message = "koordinatlar turkıye sınırları dısındadır")
    private Double latitude;

    @NotNull(message = "longitude alanı bos olamaz")
    @DecimalMin(value = "25.0", message = "koordinatlar turkıye sınırları dısındadır")
    @DecimalMax(value = "45.0", message = "koordinatlar turkıye sınırları dısındadır")
    private Double longitude;

    @NotNull(message = "PinTuru alanı bos olamaz")
    private PinTuru pinTuru;
}