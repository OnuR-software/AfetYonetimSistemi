package AfetYonetimSistemi.DTO;

import AfetYonetimSistemi.Model.Role;
import lombok.Data;

@Data
public class LoginResponse {

    private String token;
    private Role role;
    private boolean ilkGiris;
}
