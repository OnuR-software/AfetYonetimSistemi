package library_management.com.DTO;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class KaynakTalepKalemDTO {

    @Size(min = 1 , message = "Malzeme id'si pozitif olmalıdır")
    private  Long malzemeId;

    @Size(min = 1 , max = 10000, message = "Malzeme miktarı minimum 1 olmalıdır")
    private Integer miktar;

}
