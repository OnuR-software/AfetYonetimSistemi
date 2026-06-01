package library_management.com.DTO;

import library_management.com.Model.DepoModeli;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AllDepoMalzemeResponse {

    private Long depoId;
    private String depoAdi;
    private DepoModeli depoModeli;
    private String ilAdı;
    private List<MalzemeResponseDTO> malzemeler;
}
