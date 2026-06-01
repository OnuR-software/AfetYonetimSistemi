package library_management.com.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DepoOneriResponse {

    private Long depoId;

    private String depoAdi;

    private double mesafeKm;  // ← sıralama buna göre olacak

    private List<MalzemeResponseDTO> malzemeler;

}
