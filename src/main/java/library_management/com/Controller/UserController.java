package library_management.com.Controller;

import jakarta.validation.Valid;
import library_management.com.DTO.PinResponseDTO;
import library_management.com.DTO.SifreYenileRequest;
import library_management.com.DTO.UserTalepKalemDTO;
import library_management.com.DTO.UserTalepResponse;
import library_management.com.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/talep")
    public ResponseEntity<String> talepOlustur(@RequestBody @Valid UserTalepKalemDTO userTalepKalemDTO) {
        return ResponseEntity.ok(userService.KullanıcıTalepOlusturma(userTalepKalemDTO));
    }

    @GetMapping("/pinler")
    public ResponseEntity<List<PinResponseDTO>> getPinler() {
        return ResponseEntity.ok(userService.getAllPinler());
    }

    @GetMapping("/talepler/aktif")
    public ResponseEntity<List<UserTalepResponse>> aktifTalepler() {
        return ResponseEntity.ok(userService.AktifTalepler());
    }

    @GetMapping("/talepler/tamamlanan")
    public ResponseEntity<List<UserTalepResponse>> tamamlananTalepler() {
        return ResponseEntity.ok(userService.TamamlananTalepler());
    }

    @PutMapping("/talep/{talepId}/iptal")
    public ResponseEntity<Void> talepIptal(@PathVariable Long talepId) {
        userService.TalepIptali(talepId);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/sifre-yenileme")
    public ResponseEntity<Void> sifreYenileme (@RequestBody @Valid SifreYenileRequest sifreYenileRequest) {
        userService.SifreYenileme(sifreYenileRequest);
        return ResponseEntity.ok().build();
    }
}