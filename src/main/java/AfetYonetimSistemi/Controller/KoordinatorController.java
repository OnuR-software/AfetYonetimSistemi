package AfetYonetimSistemi.Controller;

import AfetYonetimSistemi.DTO.*;
import jakarta.validation.Valid;
import AfetYonetimSistemi.Service.KoordinatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/koordinator")
public class KoordinatorController {

    private final KoordinatorService koordinatorService;

    @GetMapping("/talepler/aktif")
    public ResponseEntity<List<KoordinatorTaleplerResponse>> aktifTalepler() {
        return ResponseEntity.ok(koordinatorService.AktifTalepler());
    }

    @GetMapping("/talepler/gecmis")
    public ResponseEntity<List<KoordinatorTaleplerResponse>> gecmisTalepler() {
        return ResponseEntity.ok(koordinatorService.GecmisTalepler());
    }

    @GetMapping("/depolar")
    public ResponseEntity<List<DepoResponse>> depolar() {
        return ResponseEntity.ok(koordinatorService.DepolarıGorme());
    }

    @PutMapping("/talep/{talepId}/onayla/{depoId}")
    public ResponseEntity<Void> talepOnayla(@PathVariable Long talepId, @PathVariable Long depoId) {
        koordinatorService.TalepOnaylama(talepId, depoId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/talep/{talepId}/reddet")
    public ResponseEntity<Void> talepReddet(@PathVariable Long talepId,
                                            @RequestBody UserTalepRedRequest sebep) {
        koordinatorService.talepReddetme(talepId, sebep);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/talep/{talepId}/depo-onerisi")
    public ResponseEntity<List<DepoOneriResponse>> enYakinDepoOnerisi(@PathVariable Long talepId) {
        return ResponseEntity.ok(koordinatorService.EnYakınDepoOnerisi(talepId));
    }

    @PutMapping("/depo/{depoId}/pasife-al")
    public ResponseEntity<Void> depoPasif(@PathVariable Long depoId) {
        koordinatorService.DepoPasifeAlma(depoId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/depo/{depoId}/aktifleştir")
    public ResponseEntity<Void> depoAktif(@PathVariable Long depoId) {
        koordinatorService.DepoAktifleştirme(depoId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/afet/gecmis")
    public ResponseEntity<List<AfetResponse>> gecmisAfetler() {
        return ResponseEntity.ok(koordinatorService.GecmisAfetleriGorme());
    }

    @GetMapping("/afet/aktif")
    public ResponseEntity<List<AfetResponse>> aktifAfetler() {
        return ResponseEntity.ok(koordinatorService.AktifAfetleriGorme());
    }

    @GetMapping("/sanal-depolar")
    public ResponseEntity<List<DepoResponse>> sanalDepolar() {
        return ResponseEntity.ok(koordinatorService.SanalDepolarıGorme());
    }

    @PutMapping("/sanal-depo/{depoId}/gorevli/{gorevliId}")
    public ResponseEntity<Void> gorevliAta(@PathVariable Long depoId, @PathVariable Long gorevliId) {
        koordinatorService.SanalDepolaraGorevliAtama(depoId, gorevliId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sanal-depo/gorevliler")
    public ResponseEntity<List<DepoGorevliResponse>> sanalDepoGorevliler() {
        return ResponseEntity.ok(koordinatorService.SanalDepoGorevliler());
    }

    @PostMapping("/depo/{depoId}/yardim-talebi")
    public ResponseEntity<Void> yardimTalebiOlustur(@PathVariable Long depoId,
                                                     @RequestBody @Valid KaynakTalepRequest request) {
        koordinatorService.DepoYardımıTalebiOlusturma(request, depoId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/depo-yardim-talepleri/aktif")
    public ResponseEntity<List<DepoToDepoTransferResponse>> aktifDepoYardimTalepleri() {
        return ResponseEntity.ok(koordinatorService.AktifDepoYardımıTalepleriniGorme());
    }

    @GetMapping("/depo-yardim-talepleri/gecmis")
    public ResponseEntity<List<DepoToDepoTransferResponse>> gecmisDepoYardimTalepleri() {
        return ResponseEntity.ok(koordinatorService.GecmisDepoYardımTalepleriniGorme());
    }

    @PutMapping("/depo-yardim-talepleri/{talepId}/iptal")
    public ResponseEntity<Void> transferIptal(@PathVariable Long talepId) {
        koordinatorService.DepolarArasıTransferIptali(talepId);
        return ResponseEntity.ok().build();
    }
}