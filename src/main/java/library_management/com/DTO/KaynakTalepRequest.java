package library_management.com.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import library_management.com.Model.Oncelik;
import lombok.Data;

import java.util.List;

@Data
public class KaynakTalepRequest {

    @NotBlank(message = "Oncelik mutlaka belirtilmeli")
    private Oncelik oncelik;
    @Size(max = 100 , message = "Mesaj boyutu en fazla 100 karekter olabılır")
    private String aciklama;

    @NotEmpty(message = "Istenilen kaynaklar secilmeli bos olamaz")
    private List<KaynakTalepKalemDTO> kalemler;

}
