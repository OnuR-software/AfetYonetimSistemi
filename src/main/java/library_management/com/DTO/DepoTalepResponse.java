package library_management.com.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class DepoTalepResponse {

    private Long talepId;

    private List<DepoTalepMalzeme> malzemeler;
}
