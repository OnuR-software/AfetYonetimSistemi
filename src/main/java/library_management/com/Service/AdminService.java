package library_management.com.Service;

import jakarta.transaction.Transactional;
import library_management.com.DTO.*;
import library_management.com.Entity.*;
import library_management.com.Exception.*;
import library_management.com.Model.DepoModeli;
import library_management.com.Model.Role;
import library_management.com.Model.TransferDurumu;
import library_management.com.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final ILRepository ilRepository;
    private final KoordinatorRepository koordinatorRepository;
    private final DepoRepository depoRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepoGorevlisiRepository depoGorevlisiRepository;
    private final AfetILRepository afetILRepository;
    private final AfetRepository afetRepository;
    private final KaynakTalepRepository kaynakTalepRepository;
    private final DistanceCalculator distanceCalculator;
    private final DepoMalzemeRepository depoMalzemeRepository;
    @Value("${app.gecici-sifre}")
    private String geciciSifre;

    @Transactional
    public void KoordinatorOlusturma (KoordinatorOlusturmaRequest koordinatorOlusturmaRequest) {
        IL gorevYaptıgıIl = ilRepository.findByIlAdi(koordinatorOlusturmaRequest.getGorevYaptıgıIl())
                .orElseThrow(() -> new NotFoundILException("Boyle bir isme ait il bulunamadı : " + koordinatorOlusturmaRequest.getGorevYaptıgıIl()));

        Koordinator koordinator = Koordinator.builder()
                .tc(koordinatorOlusturmaRequest.getTc())
                .password(passwordEncoder.encode(geciciSifre))
                .telNo(koordinatorOlusturmaRequest.getTelNo())
                .role(Role.KOORDINATOR)
                .ilkGiris(true)
                .username(koordinatorOlusturmaRequest.getUsername())
                .email(koordinatorOlusturmaRequest.getEmail())
                .gorevYaptıgıİl(gorevYaptıgıIl)
                .build();
        koordinatorRepository.save(koordinator);
    }

    public void DepoOlusturma (DepoOlusturmaRequest depoOlusturmaRequest) {
        IL il = ilRepository.findByIlAdi(depoOlusturmaRequest.getIl())
                .orElseThrow(() -> new NotFoundILException("Bu isimde bir il bulunamadı : " + depoOlusturmaRequest.getIl()));
        if (depoOlusturmaRequest.getDepoModeli() == DepoModeli.SANAL) {
            Depo depo = Depo.builder()
                    .depoAdi(depoOlusturmaRequest.getDepoAdi())
                    .depoModeli(depoOlusturmaRequest.getDepoModeli())
                    .longitude(depoOlusturmaRequest.getLongitude())
                    .latitude(depoOlusturmaRequest.getLatitude())
                    .aktif(false)
                    .il(il)
                    .build();
            depoRepository.save(depo);
        } else {
            Depo depo = Depo.builder()
                    .depoAdi(depoOlusturmaRequest.getDepoAdi())
                    .depoModeli(depoOlusturmaRequest.getDepoModeli())
                    .longitude(depoOlusturmaRequest.getLongitude())
                    .latitude(depoOlusturmaRequest.getLatitude())
                    .aktif(true)
                    .il(il)
                    .build();
            depoRepository.save(depo);
        }

    }

    public List<AllDepoMalzemeResponse> TumDepolarıGorme() {

        List<Depo> depolar = depoRepository.findAllByAktif(true);

        return depolar.stream()
                .map(k-> AllDepoMalzemeResponse.builder()
                        .depoId(k.getId())
                        .depoAdi(k.getDepoAdi())
                        .depoModeli(k.getDepoModeli())
                        .ilAdı(k.getIl().getIlAdi())
                                .malzemeler(k.getDepoMalzeme().stream()
                                        .filter(x-> k.isAktif())
                                        .map(x-> MalzemeResponseDTO.builder()
                                                .id(x.getMalzeme().getId())
                                                .stok(x.KullanılabilirStokMiktari())
                                                .malzemeDurumu(x.getMalzemeDurumu())
                                                .malzemeAdi(x.getMalzeme().getMalzemeAdi())
                                                .malzemeKategori(x.getMalzeme().getMalzemeKategori())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build()
                        )
                .collect(Collectors.toList());
    }

    @Transactional
    public void DepoGorevlisiOlusturma (KoordinatorOlusturmaRequest koordinatorOlusturmaRequest) {
        IL gorevYaptıgıIl = ilRepository.findByIlAdi(koordinatorOlusturmaRequest.getGorevYaptıgıIl())
                .orElseThrow(() -> new NotFoundILException("Boyle bir isme ait il bulunamadı : " + koordinatorOlusturmaRequest.getGorevYaptıgıIl()));

        DepoGorevlisi depoGorevlisi = DepoGorevlisi.builder()
                .tc(koordinatorOlusturmaRequest.getTc())
                .password(passwordEncoder.encode(geciciSifre))
                .telNo(koordinatorOlusturmaRequest.getTelNo())
                .role(Role.DEPO_SORUMLUSU)
                .ilkGiris(true)
                .username(koordinatorOlusturmaRequest.getUsername())
                .email(koordinatorOlusturmaRequest.getEmail())
                .gorevYaptıgıIl(gorevYaptıgıIl)
                .build();
        depoGorevlisiRepository.save(depoGorevlisi);
    }

    public void afetAktifleştirme (AfetAktifleştirmeRequest afetAktifleştirmeRequest) {
            IL il = ilRepository.findByIlAdi(afetAktifleştirmeRequest.getIl())
                    .orElseThrow(() -> new NotFoundILException("Bu isimde bir il bulunamadı : " + afetAktifleştirmeRequest.getIl()));
            Afet afett = afetRepository.findById(afetAktifleştirmeRequest.getAfetId())
                    .orElseThrow(() -> new NotFoundAfetException("Bu idye ait afet bulunamadı : " + afetAktifleştirmeRequest.getAfetId()));

            AfetIL afet = AfetIL.builder()
                    .afet(afett)
                    .il(il)
                    .aktif(true)
                    .baslangicTarihi(LocalDateTime.now())
                    .build();
            afetILRepository.save(afet);
    }

    public List<AfetAktifResponse> AktifAfetleriGorme () {
            List<AfetIL> afetler = afetILRepository.findAllByAktif(true);
            return afetler.stream()
                    .map(k-> AfetAktifResponse.builder()
                            .afetId(k.getId())
                            .ilAdı(k.getIl().getIlAdi())
                            .afetTuru(k.getAfet().getAfetTuru())
                            .baslangicTarihi(k.getBaslangicTarihi())
                            .build()
                    )
                    .collect(Collectors.toList());
    }

    public void AfetIptali (Long afetId) {
            AfetIL afet = afetILRepository.findByIdAndAktif(afetId , true)
                    .orElseThrow(() -> new NotFoundAfetException("Bu idye ait afet bulunamadı : " + afetId));
            afet.setAktif(false);
            afet.setBitisTarihi(LocalDateTime.now());
            afetILRepository.save(afet);
    }

    public List<KaynakTalepResponse> TransferTalepleri() {
            List<KaynakTalep> talepler = kaynakTalepRepository.findAllByAktifAndTransferDurumu(true , TransferDurumu.BEKLEMEDE);
            return   talepler.stream()
                    .map(k-> KaynakTalepResponse.builder()
                            .acıklama(k.getAcıklama())
                            .oncelik(k.getOncelik())
                            .kaynakTalepId(k.getId())
                            .olusturulmaTarihi(k.getOlusturulma_tarihi())
                            .kalemler(k.getKalemler().stream()
                                    .map(x-> KaynakTalepKalemResponse.builder()
                                            .malzemeId(x.getMalzeme().getId())
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

    public List<DepoOneriResponse> EnYakınDepoOnerisi(Long talepId) {

        KaynakTalep kaynakTalep = kaynakTalepRepository.findById(talepId)
                .orElseThrow(() -> new NotFoundTalep("Bu idye ait talep bulunamadı : "+ talepId ));

        List<Depo> depolar = depoRepository.findAllByAktifAndDepoModeliIn(true , List.of(DepoModeli.MERKEZİ , DepoModeli.FIZIKSEL));

        return depolar.stream()
                .filter(k-> MalzemeKarsılayabilirMi(k , kaynakTalep))
                .map(depo -> {
                    double mesafe = distanceCalculator.calculate(kaynakTalep.getYardımAlanDepo().getLatitude() ,kaynakTalep.getYardımAlanDepo().getLongitude() ,
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

    public void talepOnaylama (Long talepId , Long depoId) {
        User user = userRepository.findByUuid(UUID.fromString(userService.MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        Admin admin = (Admin) user;

        KaynakTalep kaynakTalep = kaynakTalepRepository.findById(talepId)
                .orElseThrow(() -> new NotFoundTalep("Bu idye ait talep bulunamadı : "+ talepId ));
        Depo yardımıGoturen = depoRepository.findById(depoId)
                .orElseThrow(() -> new NotFoundDepo("Bu idye ait depo bulunamadı : " + depoId));
        kaynakTalep.setGuncelleme_tarihi(LocalDateTime.now());
        kaynakTalep.setTransferDurumu(TransferDurumu.ONAYLANDI);
        kaynakTalep.setAdmin(admin);
        kaynakTalep.setYardımGoturenDepo(yardımıGoturen);
        kaynakTalepRepository.save(kaynakTalep);
    }

    public void talepIptali (Long talepId , KaynakTalepIptalRequest kaynakTalepIptalRequest ) {
        User user = userRepository.findByUuid(UUID.fromString(userService.MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        Admin admin = (Admin) user;

        KaynakTalep kaynakTalep = kaynakTalepRepository.findById(talepId)
                .orElseThrow(() -> new NotFoundTalep("Bu idye ait talep bulunamadı : "+ talepId ));

        kaynakTalep.setAktif(false);
        kaynakTalep.setTransferDurumu(TransferDurumu.REDDEDILDI);
        kaynakTalep.setAdmin(admin);
        kaynakTalep.setAdminNotu(kaynakTalepIptalRequest.getAciklama());
        kaynakTalep.setGuncelleme_tarihi(LocalDateTime.now());
        kaynakTalepRepository.save(kaynakTalep);
    }

    public boolean MalzemeKarsılayabilirMi (Depo depo , KaynakTalep talep) {
        for (KaynakTalepKalem kaynakTalepKalem : talep.getKalemler()) {
            DepoMalzeme depoMalzeme = depoMalzemeRepository.findByDepoAndMalzeme(depo , kaynakTalepKalem.getMalzeme())
                    .orElse(null);
            if (depoMalzeme == null) return false;
            int gidecekYardımSayisi = kaynakTalepKalem.getMiktar();
            if (depoMalzeme.KullanılabilirStokMiktari() < gidecekYardımSayisi) return false;
        }
        return true;
    }


}
