package library_management.com.Controller;

import jakarta.validation.Valid;
import library_management.com.DTO.DepoTalepResponse;
import library_management.com.DTO.DepoToDepoTransferResponse;
import library_management.com.DTO.MalzemeResponseDTO;
import library_management.com.DTO.StokRequestDTO;
import library_management.com.Service.DepoGorevlisiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/depo-gorevlisi")
public class DepoGorevlisiController {

    private final DepoGorevlisiService depoGorevlisiService;

    @GetMapping("/malzeme")
    public ResponseEntity<List<MalzemeResponseDTO>> getMalzeme() {
        return ResponseEntity.ok(depoGorevlisiService.getAllMalzeme());
    }

    @PostMapping("/stok")
    public ResponseEntity<Void> stokGirisi(@RequestBody @Valid StokRequestDTO stokRequestDTO) {
        depoGorevlisiService.StokGirisi(stokRequestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/talepler/gonderilecek")
    public ResponseEntity<List<DepoTalepResponse>> gonderilecekTalepler() {
        return ResponseEntity.ok(depoGorevlisiService.GonderilecekTalepler());
    }

    @PutMapping("/talep/{talepId}/hazirla")
    public ResponseEntity<Void> talepHazirla(@PathVariable Long talepId) {
        depoGorevlisiService.TaleplerinHazırlanması(talepId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/talep/{talepId}/yola-cik")
    public ResponseEntity<Void> talepYolaCik(@PathVariable Long talepId) {
        depoGorevlisiService.TaleplerinYolaCıkması(talepId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/talep/{talepId}/teslim-edildi")
    public ResponseEntity<Void> talepTeslimEdildi(@PathVariable Long talepId) {
        depoGorevlisiService.taleplerinUlasması(talepId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/depolar-arasi-transfer/aktif")
    public ResponseEntity<List<DepoToDepoTransferResponse>> depolarArasiAktifTransferler() {
        return ResponseEntity.ok(depoGorevlisiService.DepolarArasıAktifTransferTalepleri());
    }

    @PutMapping("/depolar-arasi-transfer/{talepId}/hazirla")
    public ResponseEntity<Void> depolarArasiTransferHazirla(@PathVariable Long talepId) {
        depoGorevlisiService.DepolarArasıTransferHazırlama(talepId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/depolar-arasi-transfer/{talepId}/yola-cik")
    public ResponseEntity<Void> depolarArasiTransferYolaCik(@PathVariable Long talepId) {
        depoGorevlisiService.DepolarArasıTransferYolaCıktı(talepId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/depolar-arasi-transfer/{talepId}/teslim-edildi")
    public ResponseEntity<Void> depolarArasiTransferTeslimEdildi(@PathVariable Long talepId) {
        depoGorevlisiService.DepolarArasıTransferTeslimEdildi(talepId);
        return ResponseEntity.ok().build();
    }
}