package library_management.com.DTO;

import library_management.com.Model.MalzemeDurumu;
import library_management.com.Model.MalzemeKategori;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MalzemeResponseDTO {

    private Long id;
    private String malzemeAdi;
    private MalzemeKategori malzemeKategori;
    private Integer stok;
    private MalzemeDurumu malzemeDurumu;
}
