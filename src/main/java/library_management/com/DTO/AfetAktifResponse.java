package library_management.com.DTO;

import library_management.com.Model.AfetTuru;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class AfetAktifResponse {

    private Long afetId;

    private AfetTuru afetTuru;

    private String ilAdı;

    private LocalDateTime baslangicTarihi;

}
