package library_management.com.DTO;

import library_management.com.Model.TransferDurumu;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserTalepResponse {

    private Long talep_id;

    private TransferDurumu durum;

    private List<String> malzemeAdi;
}
