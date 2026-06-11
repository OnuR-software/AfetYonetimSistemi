package AfetYonetimSistemi.Service;

import AfetYonetimSistemi.Entity.*;
import AfetYonetimSistemi.Exception.*;
import jakarta.transaction.Transactional;
import AfetYonetimSistemi.DTO.PinResponseDTO;
import AfetYonetimSistemi.DTO.SifreYenileRequest;
import AfetYonetimSistemi.DTO.UserTalepKalemDTO;
import AfetYonetimSistemi.DTO.UserTalepResponse;
import library_management.com.Entity.*;
import library_management.com.Exception.*;
import AfetYonetimSistemi.Model.TransferDurumu;
import AfetYonetimSistemi.Repository.ILRepository;
import AfetYonetimSistemi.Repository.MalzemeRepository;
import AfetYonetimSistemi.Repository.UserRepository;
import AfetYonetimSistemi.Repository.UserTalepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
        private final UserRepository userRepository;
        private final MalzemeRepository malzemeRepository;
        private final UserTalepRepository userTalepRepository;
        private final GeocodingService geocodingService;
        private final ILRepository ilRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String KullanıcıTalepOlusturma (UserTalepKalemDTO userTalepKalemDTO) {
        User user = userRepository.findByUuid(UUID.fromString(MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
                if (user.getPinSayısı() >= 3) throw new NotLimitPinsException("Yardım talebi sınırına ulasıldı");
        List<UserTalepKalem> userTalepKalemDTOList = userTalepKalemDTO.getMalzemeId()
                .stream()
                .map(k -> UserTalepKalem.builder()
                        .malzeme(malzemeRepository.findById(k)
                                .orElseThrow(() -> new NotFoundMalzemeException("Malzeme bulunamadı id : " + k))
                        )
                        .build()
                )
                .collect(Collectors.toList());

            UserTalep userTalep = UserTalep.builder()
                    .user(user)
                    .kisiSayısı(userTalepKalemDTO.getKisiSayısı())
                    .Olusturulma_tarihi(LocalDateTime.now())
                    .kalemler(userTalepKalemDTOList)
                    .pinler(Pinler.builder()
                            .latitude(userTalepKalemDTO.getPinRequestDTO().getLatitude())
                            .longitude(userTalepKalemDTO.getPinRequestDTO().getLongitude())
                            .pinTuru(userTalepKalemDTO.getPinRequestDTO().getPinTuru())
                            .user(user)
                            .build()
                    )
                    .build();
            userTalepKalemDTOList.forEach(k -> k.setUserTalep(userTalep));
            IL il = ilRepository.findByIlAdi(geocodingService.getIl(userTalep.getPinler().getLatitude(), userTalep.getPinler().getLongitude()))
                    .orElseThrow(() -> new NotFoundILException("Boyle bir isme ait il bulunamadı : " +
                            geocodingService.getIl(userTalep.getPinler().getLatitude(), userTalep.getPinler().getLongitude())));
            userTalep.setMevcutIl(il);

            int yardımSayısı = userTalep.getKalemler().size();
            if (user.getPinSayısı()+yardımSayısı > 3) throw new NotLimitPinsException("Yardım talebi sınırına ulasıldı");

            userTalepRepository.save(userTalep);
            user.setPinSayısı(user.getPinSayısı() + yardımSayısı);
            userRepository.save(user);

        return "Kayıt Başarılı bir şekilde gercekleşti";
    }

    public List<PinResponseDTO> getAllPinler () {
        User user = userRepository.findByUuid(UUID.fromString(MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        List<PinResponseDTO> pinler = user.getPinler()
                .stream()
                .filter(k -> k.isAktif())
                .map(k -> PinResponseDTO.builder()
                        .pinTuru(k.getPinTuru())
                        .longitude(k.getLongitude())
                        .latitude(k.getLatitude())
                        .id(k.getId())
                        .build())
                .collect(Collectors.toList());
        return pinler;
    }

    public List<UserTalepResponse> AktifTalepler () {
        User user = userRepository.findByUuid(UUID.fromString(MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        List<UserTalep> taleps = userTalepRepository.findByUser_Id(user.getId());
        List<UserTalepResponse> responses  = taleps.stream()
                .filter(k -> k.isAktif())
                .map(k-> UserTalepResponse.builder()
                        .durum(k.getTransferDurumu())
                        .talep_id(k.getId())
                        .malzemeAdi(k.getKalemler().stream()
                                .map(x-> x.getMalzeme().getMalzemeAdi())
                                .collect(Collectors.toList())
                        )
                        .build())
                .collect(Collectors.toList());

        return responses ;
    }

    public List<UserTalepResponse> TamamlananTalepler () {
        User user = userRepository.findByUuid(UUID.fromString(MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        List<UserTalep> taleps = userTalepRepository.findByUser_Id(user.getId());
        List<UserTalepResponse> responses  = taleps.stream()
                .filter(k -> !k.isAktif())
                .map(k-> UserTalepResponse.builder()
                        .durum(k.getTransferDurumu())
                        .talep_id(k.getId())
                        .malzemeAdi(k.getKalemler().stream()
                                .map(x-> x.getMalzeme().getMalzemeAdi())
                                .collect(Collectors.toList())
                        )
                        .build())
                .collect(Collectors.toList());

        return responses ;
    }
    @Transactional
    public void TalepIptali (Long talepId) {
        User user = userRepository.findByUuid(UUID.fromString(MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        UserTalep talep = userTalepRepository.findByIdAndTransferDurumu(talepId , TransferDurumu.BEKLEMEDE)
                .orElseThrow(() -> new NotFoundUserTalepException("Bu talep iptal edilemez : " + talepId));
        talep.setAktif(false);
        talep.setTransferDurumu(TransferDurumu.IPTAL_EDILDI);
        talep.setGuncelleme_tarihi(LocalDateTime.now());
        talep.getPinler().setAktif(false);
        userTalepRepository.save(talep);
        user.setPinSayısı(user.getPinSayısı() - talep.getKalemler().size());
        userRepository.save(user);
    }

    public String MevcutKullanici () {
        String UUID = SecurityContextHolder.getContext().getAuthentication().getName();
        return UUID;
    }

    public void SifreYenileme (SifreYenileRequest sifreYenileRequest) {
        User user = userRepository.findByUuid(UUID.fromString(MevcutKullanici()))
                .orElseThrow(() -> new NotFoundUserException("Kullanıcı bulunamadı"));
        user.setPassword(passwordEncoder.encode(sifreYenileRequest.getSifre()));
        user.setIlkGiris(false);
        userRepository.save(user);
    }
}
