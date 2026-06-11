package AfetYonetimSistemi.Controller;

import jakarta.validation.Valid;
import AfetYonetimSistemi.DTO.KodGonderRequest;
import AfetYonetimSistemi.DTO.LoginRequest;
import AfetYonetimSistemi.DTO.LoginResponse;
import AfetYonetimSistemi.DTO.SifremiUnuttumRequest;
import AfetYonetimSistemi.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Void> registerOrLogin(@RequestBody @Valid LoginRequest loginRequest) {
        authService.login(loginRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<LoginResponse> verify(@RequestBody @Valid KodGonderRequest kodGonderRequest) {
        LoginResponse response = authService.KodOnayı(kodGonderRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/passwordRegen")
    public ResponseEntity<Void> PasswordRegen(@RequestBody @Valid SifremiUnuttumRequest loginRequest) {
        authService.SifremiUnuttum(loginRequest);
        return ResponseEntity.ok().build();
    }
}
