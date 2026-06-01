package library_management.com.Controller;

import jakarta.validation.Valid;
import library_management.com.DTO.KodGonderRequest;
import library_management.com.DTO.LoginRequest;
import library_management.com.DTO.LoginResponse;
import library_management.com.DTO.SifremiUnuttumRequest;
import library_management.com.Service.AuthService;
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
