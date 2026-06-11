package AfetYonetimSistemi.Service;

import AfetYonetimSistemi.DTO.*;
import AfetYonetimSistemi.Entity.*;
import AfetYonetimSistemi.Exception.*;
import AfetYonetimSistemi.Repository.*;
import jakarta.transaction.Transactional;
import AfetYonetimSistemi.DTO.*;
import AfetYonetimSistemi.Entity.*;
import AfetYonetimSistemi.Exception.*;
import AfetYonetimSistemi.Model.TransferDurumu;
import AfetYonetimSistemi.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepoGorevlisiService {

    private final UserTalepRepository userTalepRepository;
    private final MalzemeRepository malzemeRepository;
    private final UserRepository userRepository;
    private final StokGirisRepository stokGirisRepository;
    private final DepoMalzemeRepository depoMalzemeRepository;
    private final KaynakTalepRepository kaynakTalepRepository;

    public List<MalzemeResponseDTO> getAllMalzeme(){
        User user = userRepository.findByUuid(UUID.fromString(MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        DepoGorevlisi depoGorevlisi = (DepoGorevlisi) user ;
        List<DepoMalzeme> depoMalzeme = depoGorevlisi.getDepo().getDepoMalzeme();
       List<MalzemeResponseDTO> malzemeResponses = depoMalzeme.stream()
               .filter(k -> k.getMalzeme().isAktif())
               .map(k -> MalzemeResponseDTO.builder()
                               .id(k.getMalzeme().getId())
                               .malzemeAdi(k.getMalzeme().getMalzemeAdi())
                                .stok(k.getStokMiktari())
                               .malzemeKategori(k.getMalzeme().getMalzemeKategori())
                                .malzemeDurumu(k.getMalzemeDurumu())
                               .build()
               )
               .collect(Collectors.toList());

            return malzemeResponses;
    }

    @Transactional
    public void  StokGirisi (StokRequestDTO stokRequestDTO) {
        User user = userRepository.findByUuid(UUID.fromString(MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        DepoGorevlisi depoGorevlisi = (DepoGorevlisi) user ;
        Malzeme malzeme = malzemeRepository.findById(stokRequestDTO.getMalzemeId())
                .orElseThrow(() -> new NotFoundMalzemeException("bu idye sahip malzeme bulunamadı :" + stokRequestDTO.getMalzemeId()));
        StokGiris stokGiris = StokGiris.builder()
                .tarih(LocalDateTime.now())
                .miktar(stokRequestDTO.getMiktar())
                .depoGorevlisi(depoGorevlisi)
                .BagıscıAdı(stokRequestDTO.getBagıscıAdı())
                .kaynakTuru(stokRequestDTO.getKaynakTuru())
                .malzeme(malzeme)
                .build();
        stokGirisRepository.save(stokGiris);
        DepoMalzeme depoMalzeme = depoMalzemeRepository.findByDepoAndMalzeme(depoGorevlisi.getDepo() , malzeme)
                .orElseThrow(()-> new NotFoundDepoMalzemeException("Depoda boyle bir malzeme bulunamadı"));
        depoMalzeme.setStokMiktari(depoMalzeme.getStokMiktari() + stokRequestDTO.getMiktar());
        depoMalzeme.updateMalzemeDurumu();
        depoMalzemeRepository.save(depoMalzeme);
    }

    public List<DepoTalepResponse> GonderilecekTalepler() {
        User user = userRepository.findByUuid(UUID.fromString(MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        DepoGorevlisi depoGorevlisi = (DepoGorevlisi) user ;

        List<UserTalep> talep = userTalepRepository.findAllByDepoAndAktifAndTransferDurumu(depoGorevlisi.getDepo() , true , TransferDurumu.ONAYLANDI);

        return talep.stream()
                .map(k -> DepoTalepResponse.builder()
                        .talepId(k.getId())
                        .malzemeler(k.getKalemler().stream()
                                .map(x -> {
                                     int gidecekYardımSayisi = (int) Math.ceil((double) k.getKisiSayısı() / x.getMalzeme().getYetecekKisiSayisi());
                                   return DepoTalepMalzeme.builder()
                                            .malzemeAdi(x.getMalzeme().getMalzemeAdi())
                                            .miktar(gidecekYardımSayisi)
                                           .build();
                                } )
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void TaleplerinHazırlanması (Long talepId) {
        User user = userRepository.findByUuid(UUID.fromString(MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        DepoGorevlisi depoGorevlisi = (DepoGorevlisi) user ;

        UserTalep talep = userTalepRepository.findByIdAndDepoAndAktif(talepId , depoGorevlisi.getDepo() , true)
                .orElseThrow(() -> new NotFoundTalep("Bu idye ait talep hazırlanma asamasına gecemedı"));

        talep.getKalemler().forEach(kalem -> {
            int gerekenAdet = (int) Math.ceil((double) talep.getKisiSayısı() / kalem.getMalzeme().getYetecekKisiSayisi());
            DepoMalzeme depoMalzeme = depoMalzemeRepository.findByDepoAndMalzeme(talep.getDepo() , kalem.getMalzeme())
                    .orElseThrow(() -> new NotFoundDepoMalzemeException("Depoda boyle bir malzeme mevcut degıl : " + kalem.getMalzeme().getMalzemeAdi()));
            if (depoMalzeme.KullanılabilirStokMiktari() < gerekenAdet) throw new InvalidDepoMalzemeException("Depoda yeterli stok yok : ");
        });

        talep.getKalemler().forEach(kalem -> {
            int gerekenAdet = (int) Math.ceil((double) talep.getKisiSayısı() / kalem.getMalzeme().getYetecekKisiSayisi());
            DepoMalzeme depoMalzeme = depoMalzemeRepository.findByDepoAndMalzeme(talep.getDepo() , kalem.getMalzeme())
                    .orElseThrow(() -> new NotFoundDepoMalzemeException("Depoda boyle bir malzeme mevcut degıl : " + kalem.getMalzeme().getMalzemeAdi()));
            depoMalzeme.setRezerveStokMiktari(depoMalzeme.getRezerveStokMiktari() + gerekenAdet);
            depoMalzemeRepository.save(depoMalzeme);
        });
        talep.setTransferDurumu(TransferDurumu.HAZIRLANIYOR);
        talep.setGuncelleme_tarihi(LocalDateTime.now());
        userTalepRepository.save(talep);
    }

    @Transactional
    public void TaleplerinYolaCıkması (Long talepId) {
        User user = userRepository.findByUuid(UUID.fromString(MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı : " + talepId));
        DepoGorevlisi depoGorevlisi = (DepoGorevlisi) user ;

        UserTalep talep = userTalepRepository.findByIdAndDepoAndAktif(talepId , depoGorevlisi.getDepo() , true)
                .orElseThrow(() -> new NotFoundTalep("Bu idye ait talep kargolanma asamasına gecemedi : " + talepId));

        talep.setTransferDurumu(TransferDurumu.YOLDA);
        talep.setGuncelleme_tarihi(LocalDateTime.now());
        userTalepRepository.save(talep);
    }

    @Transactional
    public void taleplerinUlasması (Long talepId) {
        User user = userRepository.findByUuid(UUID.fromString(MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı : " + talepId));
        DepoGorevlisi depoGorevlisi = (DepoGorevlisi) user ;

        UserTalep talep = userTalepRepository.findByIdAndDepoAndAktif(talepId , depoGorevlisi.getDepo() , true)
                .orElseThrow(() -> new NotFoundTalep("Bu idye ait talep kargolanma asamasına gecemedi : " + talepId));

        talep.getKalemler().forEach(kalem -> {
            int gerekenAdet = (int) Math.ceil((double) talep.getKisiSayısı() / kalem.getMalzeme().getYetecekKisiSayisi());
            DepoMalzeme depoMalzeme = depoMalzemeRepository.findByDepoAndMalzeme(talep.getDepo() , kalem.getMalzeme())
                    .orElseThrow(() -> new NotFoundDepoMalzemeException("Depoda boyle bir malzeme mevcut degıl : " + kalem.getMalzeme().getMalzemeAdi()));
            depoMalzeme.setStokMiktari(depoMalzeme.getStokMiktari() - gerekenAdet);
            depoMalzeme.setRezerveStokMiktari(depoMalzeme.getRezerveStokMiktari() - gerekenAdet);
            depoMalzeme.updateMalzemeDurumu();
            depoMalzemeRepository.save(depoMalzeme);
        });
        talep.setTransferDurumu(TransferDurumu.TESLIM_EDILDI);
        talep.setGuncelleme_tarihi(LocalDateTime.now());
        talep.getPinler().setAktif(false);
        talep.setAktif(false);
        talep.getUser().setPinSayısı(talep.getUser().getPinSayısı() - talep.getKalemler().size());
        userTalepRepository.save(talep);
    }

    public List<DepoToDepoTransferResponse> DepolarArasıAktifTransferTalepleri () {
        User user = userRepository.findByUuid(UUID.fromString(MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı "));
        DepoGorevlisi depoGorevlisi = (DepoGorevlisi) user ;

        List<KaynakTalep> taleps = kaynakTalepRepository.findByYardımGoturenDepoAndAktifAndTransferDurumu(depoGorevlisi.getDepo() , true , TransferDurumu.ONAYLANDI);

        return taleps.stream()
                .map(k-> DepoToDepoTransferResponse.builder()
                                .talepId(k.getId())
                        .transferDurumu(k.getTransferDurumu())
                        .adminNotu(k.getAdminNotu())
                        .oncelik(k.getOncelik())
                        .longitude(k.getYardımAlanDepo().getLongitude())
                        .latitude(k.getYardımAlanDepo().getLatitude())
                        .malzemeler(k.getKalemler().stream()
                                .map(x-> DepoTalepMalzeme.builder()
                                        .miktar(x.getMiktar())
                                        .malzemeAdi(x.getMalzeme().getMalzemeAdi())
                                        .build()
                                )
                                .collect(Collectors.toList())
                        )
                                .build()
                        )
                .collect(Collectors.toList());
    }

    public void DepolarArasıTransferHazırlama (Long talepId) {
        User user = userRepository.findByUuid(UUID.fromString(MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        DepoGorevlisi depoGorevlisi = (DepoGorevlisi) user ;

        KaynakTalep talep = kaynakTalepRepository.findByIdAndTransferDurumuAndYardımGoturenDepo(talepId , TransferDurumu.ONAYLANDI , depoGorevlisi.getDepo())
                .orElseThrow(() -> new NotFoundTalep("Bu idye ait talep bulunamadı : " + talepId));
        talep.setTransferDurumu(TransferDurumu.HAZIRLANIYOR);
        talep.setGuncelleme_tarihi(LocalDateTime.now());
        kaynakTalepRepository.save(talep);
    }

    public void DepolarArasıTransferYolaCıktı (Long talepId) {
        User user = userRepository.findByUuid(UUID.fromString(MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        DepoGorevlisi depoGorevlisi = (DepoGorevlisi) user ;

        KaynakTalep talep = kaynakTalepRepository.findByIdAndTransferDurumuAndYardımGoturenDepo(talepId , TransferDurumu.HAZIRLANIYOR , depoGorevlisi.getDepo())
                .orElseThrow(() -> new NotFoundTalep("Bu idye ait talep bulunamadı : " + talepId));
        talep.setTransferDurumu(TransferDurumu.YOLDA);
        talep.setGuncelleme_tarihi(LocalDateTime.now());
        kaynakTalepRepository.save(talep);
    }

    @Transactional
    public void DepolarArasıTransferTeslimEdildi(Long talepId) {
        User user = userRepository.findByUuid(UUID.fromString(MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        DepoGorevlisi depoGorevlisi = (DepoGorevlisi) user ;

        KaynakTalep talep = kaynakTalepRepository.findByIdAndTransferDurumuAndYardımGoturenDepo(talepId , TransferDurumu.YOLDA , depoGorevlisi.getDepo())
                .orElseThrow(() -> new NotFoundTalep("Bu idye ait talep bulunamadı : " + talepId));
        talep.setTransferDurumu(TransferDurumu.TESLIM_EDILDI);
        talep.setGuncelleme_tarihi(LocalDateTime.now());
        talep.setAktif(false);
        kaynakTalepRepository.save(talep);

        talep.getKalemler().forEach(kalem -> {
            DepoMalzeme depoMalzeme = depoMalzemeRepository.findByDepoAndMalzeme(talep.getYardımGoturenDepo() , kalem.getMalzeme())
                    .orElseThrow(() -> new NotFoundDepoMalzemeException("Depoda boyle bir malzeme mevcut degıl : " + kalem.getMalzeme().getMalzemeAdi()));
            depoMalzeme.setStokMiktari(depoMalzeme.getStokMiktari() - kalem.getMiktar());
            depoMalzeme.setRezerveStokMiktari(depoMalzeme.getRezerveStokMiktari() - kalem.getMiktar());
            depoMalzeme.updateMalzemeDurumu();
            depoMalzemeRepository.save(depoMalzeme);
        });

    }

    public String MevcutKullanici () {
        String UUID = SecurityContextHolder.getContext().getAuthentication().getName();
        return UUID;
    }
}
