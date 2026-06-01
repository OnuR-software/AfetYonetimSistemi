package library_management.com.DTO;

import library_management.com.Model.AfetTuru;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AfetResponse {

    private AfetTuru afetTuru;

    private LocalDateTime baslangicTarihi;

    private LocalDateTime bitisTarihi;
}
