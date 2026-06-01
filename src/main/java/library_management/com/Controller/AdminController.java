package library_management.com.Controller;

import jakarta.validation.Valid;
import library_management.com.DTO.*;
import library_management.com.Service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/koordinator")
    public ResponseEntity<Void> koordinatorOlustur(@RequestBody @Valid KoordinatorOlusturmaRequest request) {
        adminService.KoordinatorOlusturma(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/depo")
    public ResponseEntity<Void> depoOlustur(@RequestBody @Valid DepoOlusturmaRequest request) {
        adminService.DepoOlusturma(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/depo-gorevlisi")
    public ResponseEntity<Void> depoGorevlisiOlustur(@RequestBody @Valid KoordinatorOlusturmaRequest request) {
        adminService.DepoGorevlisiOlusturma(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/depolar")
    public ResponseEntity<List<AllDepoMalzemeResponse>> tumDepolar() {
        return ResponseEntity.ok(adminService.TumDepolarıGorme());
    }

    @PostMapping("/afet/aktifleştir")
    public ResponseEntity<Void> afetAktifleştir(@RequestBody @Valid AfetAktifleştirmeRequest request) {
        adminService.afetAktifleştirme(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/afet/aktif")
    public ResponseEntity<List<AfetAktifResponse>> aktifAfetler() {
        return ResponseEntity.ok(adminService.AktifAfetleriGorme());
    }

    @PutMapping("/afet/{afetId}/iptal")
    public ResponseEntity<Void> afetIptal(@PathVariable Long afetId) {
        adminService.AfetIptali(afetId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/transfer-talepleri")
    public ResponseEntity<List<KaynakTalepResponse>> transferTalepleri() {
        return ResponseEntity.ok(adminService.TransferTalepleri());
    }

    @GetMapping("/transfer-talepleri/{talepId}/depo-onerisi")
    public ResponseEntity<List<DepoOneriResponse>> enYakinDepoOnerisi(@PathVariable Long talepId) {
        return ResponseEntity.ok(adminService.EnYakınDepoOnerisi(talepId));
    }

    @PutMapping("/transfer-talepleri/{talepId}/onayla/{depoId}")
    public ResponseEntity<Void> talepOnayla(@PathVariable Long talepId, @PathVariable Long depoId) {
        adminService.talepOnaylama(talepId, depoId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/transfer-talepleri/{talepId}/reddet")
    public ResponseEntity<Void> talepReddet(@PathVariable Long talepId,
                                            @RequestBody @Valid KaynakTalepIptalRequest request) {
        adminService.talepIptali(talepId, request);
        return ResponseEntity.ok().build();
    }
}