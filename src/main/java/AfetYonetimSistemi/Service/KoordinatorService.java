package AfetYonetimSistemi.Service;

import AfetYonetimSistemi.DTO.*;
import AfetYonetimSistemi.Entity.*;
import AfetYonetimSistemi.Exception.*;
import AfetYonetimSistemi.Repository.*;
import library_management.com.DTO.*;
import library_management.com.Entity.*;
import library_management.com.Exception.*;
import AfetYonetimSistemi.Model.DepoModeli;
import AfetYonetimSistemi.Model.TransferDurumu;
import library_management.com.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KoordinatorService {
    private final MalzemeRepository malzemeRepository;
    private final KaynakTalepRepository kaynakTalepRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserTalepRepository userTalepRepository;
    private final DepoRepository depoRepository;
    private final DistanceCalculator distanceCalculator;
    private final DepoMalzemeRepository depoMalzemeRepository;
    private final AfetILRepository afetILRepository;
    private final DepoGorevlisiRepository depoGorevlisiRepository;

    public List<KoordinatorTaleplerResponse> AktifTalepler () {
        User user = userRepository.findByUuid(UUID.fromString(userService.MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        Koordinator koordinator = (Koordinator) user;
        List<UserTalep> talepler = userTalepRepository.findAllByAktifAndMevcutIl(true , koordinator.getGorevYaptıgıİl());

        List<KoordinatorTaleplerResponse> aktifTaleplerResponses = talepler.stream()
                .map(k-> KoordinatorTaleplerResponse.builder()
                        .talepId(k.getId())
                        .kisiSayisi(k.getKisiSayısı())
                        .malzemeler(k.getKalemler().stream()
                                .map(y-> y.getMalzeme().getMalzemeAdi())
                                .collect(Collectors.toList()))
                        .pinResponseDTO(PinResponseDTO.builder()
                                .latitude(k.getPinler().getLatitude())
                                .longitude(k.getPinler().getLongitude())
                                .pinTuru(k.getPinler().getPinTuru())
                                .id(k.getPinler().getId())
                                .build()
                        )
                        .build()
                ).collect(Collectors.toList());

        return aktifTaleplerResponses ;
    }

    public List<KoordinatorTaleplerResponse> GecmisTalepler () {
        User user = userRepository.findByUuid(UUID.fromString(userService.MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        Koordinator koordinator = (Koordinator) user;

        List<UserTalep> talepler = userTalepRepository.findAllByAktifAndMevcutIl(false , koordinator.getGorevYaptıgıİl());

        List<KoordinatorTaleplerResponse> gecmisTaleplerResponses = talepler.stream()
                .map(k-> KoordinatorTaleplerResponse.builder()
                        .talepId(k.getId())
                        .kisiSayisi(k.getKisiSayısı())
                        .malzemeler(k.getKalemler().stream()
                                .map(y-> y.getMalzeme().getMalzemeAdi())
                                .collect(Collectors.toList()))
                        .pinResponseDTO(PinResponseDTO.builder()
                                .latitude(k.getPinler().getLatitude())
                                .longitude(k.getPinler().getLongitude())
                                .pinTuru(k.getPinler().getPinTuru())
                                .id(k.getPinler().getId())
                                .build()
                        )
                        .build()
                ).collect(Collectors.toList());

        return gecmisTaleplerResponses ;
    }

    public List<DepoResponse> DepolarıGorme () {
        User user = userRepository.findByUuid(UUID.fromString(userService.MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        Koordinator koordinator = (Koordinator) user;

        List<Depo> depolar = depoRepository.findByIl_Id(koordinator.getGorevYaptıgıİl().getId());

        List<DepoResponse> depoResponses = depolar.stream()
                .filter(k-> k.isAktif())
                .map(k-> DepoResponse.builder()
                        .depoId(k.getId())
                        .depoAdi(k.getDepoAdi())
                        .malzemeler(k.getDepoMalzeme().stream()
                                .map(x->  MalzemeResponseDTO.builder()
                                        .malzemeAdi(x.getMalzeme().getMalzemeAdi())
                                        .malzemeKategori(x.getMalzeme().getMalzemeKategori())
                                        .stok(x.getStokMiktari())
                                        .id(x.getMalzeme().getId())
                                        .build())
                                .collect(Collectors.toList())
                        )
                        .build()
                )
                .collect(Collectors.toList());

        return depoResponses ;
    }

    public void TalepOnaylama (Long talepId , Long depoId) {
        User user = userRepository.findByUuid(UUID.fromString(userService.MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        Koordinator koordinator = (Koordinator) user;

        UserTalep talep = userTalepRepository.findByIdAndMevcutIl(talepId , koordinator.getGorevYaptıgıİl())
                .orElseThrow(() -> new NotFoundTalep("Bu id ye ait talep bulunamadı : "+ talepId ));
        Depo Hedefdepo = depoRepository.findById(depoId)
                .orElseThrow( () -> new NotFoundDepo("Bu idye ait depo bulunamamıştır :" + depoId));
        talep.setGuncelleme_tarihi(LocalDateTime.now());
        talep.setTransferDurumu(TransferDurumu.ONAYLANDI);
        talep.setDepo(Hedefdepo);
        talep.setKoordinator(koordinator);
        userTalepRepository.save(talep);
    }

    public void talepReddetme(Long talepId , UserTalepRedRequest userTalepRedRequest) {
        User user = userRepository.findByUuid(UUID.fromString(userService.MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        Koordinator koordinator = (Koordinator) user;

        UserTalep talep = userTalepRepository.findByIdAndMevcutIl(talepId , koordinator.getGorevYaptıgıİl())
                .orElseThrow(() -> new NotFoundTalep("Bu id ye ait talep bulunamadı : "+ talepId ));
        talep.setAktif(false);
        talep.setAciklama(userTalepRedRequest.getAciklama());
        talep.setKoordinator(koordinator);
        talep.setTransferDurumu(TransferDurumu.REDDEDILDI);
        talep.setGuncelleme_tarihi(LocalDateTime.now());

        userTalepRepository.save(talep);
    }

    public List<DepoOneriResponse> EnYakınDepoOnerisi(Long talepId) {
        User user = userRepository.findByUuid(UUID.fromString(userService.MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        Koordinator koordinator = (Koordinator) user;

        UserTalep talep = userTalepRepository.findById(talepId)
                .orElseThrow(() -> new NotFoundTalep("Bu id ye ait talep bulunamadı : "+ talepId ));

        List<Depo> depolar = depoRepository.findAllByIlAndAktif(koordinator.getGorevYaptıgıİl() , true);

        return depolar.stream()
                .filter(k-> MalzemeKarsılayabilirMi(k , talep))
                .map(depo -> {
                    double mesafe = distanceCalculator.calculate(talep.getPinler().getLatitude() , talep.getPinler().getLongitude() ,
                            depo.getLatitude() , depo.getLongitude());
                    return DepoOneriResponse.builder()
                            .depoAdi(depo.getDepoAdi())
                            .depoId(depo.getId())
                            .mesafeKm(mesafe)
                            .malzemeler(depo.getDepoMalzeme().stream()
                                    .map(k -> MalzemeResponseDTO.builder()
                                            .stok(k.getStokMiktari())
                                            .malzemeAdi(k.getMalzeme().getMalzemeAdi())
                                            .id(k.getMalzeme().getId())
                                            .malzemeKategori(k.getMalzeme().getMalzemeKategori())
                                            .build())
                                    .collect(Collectors.toList())
                            )
                            .build();
                })
                .sorted(Comparator.comparingDouble(DepoOneriResponse::getMesafeKm))
                .collect(Collectors.toList());
    }

    public boolean MalzemeKarsılayabilirMi (Depo depo , UserTalep talep) {
        for (UserTalepKalem userTalepKalem : talep.getKalemler()) {
            DepoMalzeme depoMalzeme = depoMalzemeRepository.findByDepoAndMalzeme(depo , userTalepKalem.getMalzeme())
                    .orElse(null);
            if (depoMalzeme == null) return false;
            int gidecekYardımSayisi = (int) Math.ceil((double) talep.getKisiSayısı() / depoMalzeme.getMalzeme().getYetecekKisiSayisi());
            if (depoMalzeme.KullanılabilirStokMiktari() < gidecekYardımSayisi) return false;
        }
        return true;
    }

    public void DepoPasifeAlma (Long depoId) {
        User user = userRepository.findByUuid(UUID.fromString(userService.MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        Koordinator koordinator = (Koordinator) user;

        Depo depo = depoRepository.findByIdAndIl(depoId , koordinator.getGorevYaptıgıİl())
                .orElseThrow(() -> new NotFoundDepo("Bu idye ait depo bulunamadı : " + depoId));
        depo.setAktif(false);
        depoRepository.save(depo);
    }

    public void DepoAktifleştirme (Long depoId) {
        User user = userRepository.findByUuid(UUID.fromString(userService.MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        Koordinator koordinator = (Koordinator) user;

        Depo depo = depoRepository.findByIdAndIl(depoId , koordinator.getGorevYaptıgıİl())
                        .orElseThrow(() -> new NotFoundDepo("bu idye ait depo bulunamadı : " + depoId ));
        depo.setAktif(true);
        depoRepository.save(depo);
    }

    public List<AfetResponse> GecmisAfetleriGorme () {
        User user = userRepository.findByUuid(UUID.fromString(userService.MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        Koordinator koordinator = (Koordinator) user;

        List<AfetIL> afetler = afetILRepository.findAllByAktifAndIl(false , koordinator.getGorevYaptıgıİl());

        return afetler.stream()
                .map(k -> AfetResponse.builder()
                        .afetTuru(k.getAfet().getAfetTuru())
                        .baslangicTarihi(k.getBaslangicTarihi())
                        .bitisTarihi(k.getBitisTarihi())
                        .build())
                .collect(Collectors.toList());

    }

    public List<AfetResponse> AktifAfetleriGorme () {
        User user = userRepository.findByUuid(UUID.fromString(userService.MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        Koordinator koordinator = (Koordinator) user;

        List<AfetIL> afetler = afetILRepository.findAllByAktifAndIl(true , koordinator.getGorevYaptıgıİl());

        return afetler.stream()
                .map(k -> AfetResponse.builder()
                        .afetTuru(k.getAfet().getAfetTuru())
                        .baslangicTarihi(k.getBaslangicTarihi())
                        .bitisTarihi(k.getBitisTarihi())
                        .build())
                .collect(Collectors.toList());

    }

    public List<DepoResponse> SanalDepolarıGorme () {
        User user = userRepository.findByUuid(UUID.fromString(userService.MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        Koordinator koordinator = (Koordinator) user;

        List<Depo> sanalDepolar = depoRepository.findAllByAktifAndIlAndDepoModeli(true , koordinator.getGorevYaptıgıİl() , DepoModeli.SANAL);
        return sanalDepolar.stream()
                .map(k -> DepoResponse.builder()
                        .depoId(k.getId())
                        .depoAdi(k.getDepoAdi())
                        .malzemeler(null)
                        .depoModeli(DepoModeli.SANAL)
                        .build())
                .collect(Collectors.toList());
    }

    public void SanalDepolaraGorevliAtama (Long depoId , Long gorevliId) {
        User user = userRepository.findByUuid(UUID.fromString(userService.MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        Koordinator koordinator = (Koordinator) user;

        DepoGorevlisi gorevli = depoGorevlisiRepository.findByIdAndDepo(gorevliId , null)
                .orElseThrow(() -> new NotFoundGorevliException("Bu idye ait gorevli bulunamadı : " + gorevliId));
        Depo depo = depoRepository.findByIdAndDepoModeliAndIl( depoId , DepoModeli.SANAL , koordinator.getGorevYaptıgıİl() )
                .orElseThrow(() -> new NotFoundSanalDepoException("Bu idye ait sanal depo bulunamamıstır : " + depoId));

        gorevli.setDepo(depo);
        depo.setAktif(true);
        depoGorevlisiRepository.save(gorevli);
        depoRepository.save(depo);
    }

    public List<DepoGorevliResponse> SanalDepoGorevliler () {
        User user = userRepository.findByUuid(UUID.fromString(userService.MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        Koordinator koordinator = (Koordinator) user;

        List<DepoGorevlisi> gorevliler = depoGorevlisiRepository.findAllByDepoAndGorevYaptıgıIl(null , koordinator.getGorevYaptıgıİl());

        return  gorevliler.stream()
                .map(k -> DepoGorevliResponse.builder()
                        .gorevliId(k.getId())
                        .gorevliAdi(k.getUsername())
                        .email(k.getEmail())
                        .build())
                .collect(Collectors.toList());
    }

    public void DepoYardımıTalebiOlusturma (KaynakTalepRequest kaynakTalepRequest , Long depoId) {
        User user = userRepository.findByUuid(UUID.fromString(userService.MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        Koordinator koordinator = (Koordinator) user;

        Depo depo = depoRepository.findByIdAndIl(depoId , koordinator.getGorevYaptıgıİl())
                .orElseThrow(() -> new NotFoundDepo("Bu idye ait depo bulunamadı : " + depoId));
        KaynakTalep talep = KaynakTalep.builder()
                .oncelik(kaynakTalepRequest.getOncelik())
                .acıklama(kaynakTalepRequest.getAciklama())
                .koordinator(koordinator)
                .Olusturulma_tarihi(LocalDateTime.now())
                .yardımAlanDepo(depo)
                .build();
        List<KaynakTalepKalem> kalemler = kaynakTalepRequest.getKalemler().stream()
                .map(k-> KaynakTalepKalem.builder()
                        .miktar(k.getMiktar())
                        .malzeme(malzemeRepository.findById(k.getMalzemeId())
                                .orElseThrow(() -> new NotFoundMalzemeException("Bu idye ait malzeme bulunamadı : " + k.getMalzemeId())))
                        .kaynakTalep(talep)
                        .build()
                )
                .collect(Collectors.toList());
        talep.setKalemler(kalemler);
        kaynakTalepRepository.save(talep);
    }

    public List<DepoToDepoTransferResponse> AktifDepoYardımıTalepleriniGorme () {
        User user = userRepository.findByUuid(UUID.fromString(userService.MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        Koordinator koordinator = (Koordinator) user;

        List<KaynakTalep> taleps = kaynakTalepRepository.findByKoordinator_IdAndAktif(koordinator.getId() , true);

        return    taleps.stream()
                .map(k-> DepoToDepoTransferResponse.builder()
                        .talepId(k.getId())
                        .oncelik(k.getOncelik())
                        .transferDurumu(k.getTransferDurumu())
                        .adminNotu(k.getAdminNotu())
                        .longitude(k.getYardımAlanDepo().getLongitude())
                        .latitude(k.getYardımAlanDepo().getLatitude())
                        .malzemeler(k.getKalemler().stream()
                                .map(x-> DepoTalepMalzeme.builder()
                                        .malzemeAdi(x.getMalzeme().getMalzemeAdi())
                                        .miktar(x.getMiktar())
                                        .build()
                                )
                                .collect(Collectors.toList())
                        )
                        .build()
                )
                .collect(Collectors.toList());
    }

    public List<DepoToDepoTransferResponse> GecmisDepoYardımTalepleriniGorme () {
        User user = userRepository.findByUuid(UUID.fromString(userService.MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        Koordinator koordinator = (Koordinator) user;

        List<KaynakTalep> taleps = kaynakTalepRepository.findByKoordinator_IdAndAktif(koordinator.getId() , false);

        return    taleps.stream()
                .map(k-> DepoToDepoTransferResponse.builder()
                        .oncelik(k.getOncelik())
                        .transferDurumu(k.getTransferDurumu())
                        .adminNotu(k.getAdminNotu())
                        .longitude(k.getYardımAlanDepo().getLongitude())
                        .latitude(k.getYardımAlanDepo().getLatitude())
                        .malzemeler(k.getKalemler().stream()
                                .map(x-> DepoTalepMalzeme.builder()
                                        .malzemeAdi(x.getMalzeme().getMalzemeAdi())
                                        .miktar(x.getMiktar())
                                        .build()
                                )
                                .collect(Collectors.toList())
                        )
                        .build()
                )
                .collect(Collectors.toList());
    }

    public void DepolarArasıTransferIptali (Long talepId) {
        User user = userRepository.findByUuid(UUID.fromString(userService.MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        Koordinator koordinator = (Koordinator) user;

        KaynakTalep talep = kaynakTalepRepository.findByIdAndTransferDurumu(talepId , TransferDurumu.BEKLEMEDE)
                .orElseThrow(() -> new InvalidKaynakTalepException("Bu talep iptal edilemez : " + talepId));
        talep.setAktif(false);
        talep.setTransferDurumu(TransferDurumu.IPTAL_EDILDI);
        talep.setGuncelleme_tarihi(LocalDateTime.now());
        kaynakTalepRepository.save(talep);
    }

}
