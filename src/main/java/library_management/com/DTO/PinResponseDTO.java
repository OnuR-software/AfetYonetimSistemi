package library_management.com.DTO;

import jakarta.validation.constraints.NotBlank;
import library_management.com.Model.PinTuru;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PinResponseDTO {

    private Long id;

    private double latitude;


    private double longitude;


    private PinTuru pinTuru;
}
