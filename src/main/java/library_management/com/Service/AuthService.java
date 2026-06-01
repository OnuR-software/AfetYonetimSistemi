package library_management.com.Service;

import library_management.com.DTO.KodGonderRequest;
import library_management.com.DTO.LoginRequest;
import library_management.com.DTO.LoginResponse;
import library_management.com.DTO.SifremiUnuttumRequest;
import library_management.com.Entity.User;
import library_management.com.Exception.*;
import library_management.com.Model.Role;
import library_management.com.Repository.UserRepository;
import library_management.com.Util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final SmsService smsService;
        private final JwtUtil jwtUtil;
        private final PasswordEncoder passwordEncoder;

    public void login(LoginRequest loginRequest) {

            User user = userRepository.findByTc(loginRequest.getTc());

            if (user == null) {
                yeniKullaniciKaydet(loginRequest);
                return;
            }
             mevcutKullaniciGiris(loginRequest , user);
        }

        private void yeniKullaniciKaydet(LoginRequest loginRequest) {

            if (loginRequest.getTelNo() == null)
                throw new InvalidTelNoException("Geçerli bir telefon numarası giriniz");
            if (!isValid(loginRequest.getTc()))
                throw new InvalidTcException("Geçersiz tc girdiniz");
            // 6 haneli kod üretme
            String kod = String.valueOf(new Random().nextInt(900000) + 100000);
            User newUser = User.builder()
                    .tc(loginRequest.getTc())
                    .password(passwordEncoder.encode(loginRequest.getPassword()))
                    .role(Role.USER)
                    .telNo(loginRequest.getTelNo())
                    .ilkGiris(false)
                    .kod(kod)
                    .kodGecerlilikSuresi(LocalDateTime.now().plusMinutes(10))
                    .pinSayısı(0)
                    .build();
            userRepository.save(newUser);
            smsService.smsDogrulamaKoduGonder(loginRequest.getTelNo(), kod);
        }

        private void mevcutKullaniciGiris(LoginRequest loginRequest , User user) {
            boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword() , user.getPassword());
            if (!passwordMatches) throw new InvalidPasswordException("Hatalı şifre");
            if (!user.getTelNo().equals(loginRequest.getTelNo())) throw new InvalidTelNoTcException("Tc telefon numarası ile eslesmiyor");
            // 6 haneli kod üretme
            String kod = String.valueOf(new Random().nextInt(900000) + 100000);
            user.setKod(kod);
            user.setKodGecerlilikSuresi(LocalDateTime.now().plusMinutes(10));
            userRepository.save(user);
            smsService.smsDogrulamaKoduGonder(loginRequest.getTelNo(), kod);
        }

        public LoginResponse KodOnayı (KodGonderRequest kodGonderRequest) {
            User user = userRepository.findByTcAndTelNo(kodGonderRequest.getTc() , kodGonderRequest.getTelNo())
                    .orElseThrow(() -> new InvalidTelNoTcException("Tc telefon numarası ile eslesmiyor"));
            if (!user.getKod().equals(kodGonderRequest.getKod())) throw new InvalidKodException("Girilen kod geçersiz");
            if (LocalDateTime.now().isAfter(user.getKodGecerlilikSuresi())) throw new InvalidTimeLimitedException("Kodun gecerlılık suresı dolmustur");
            user.setKodGecerlilikSuresi(null);
            user.setKod(null);
            userRepository.save(user);
            return buildResponse(user);
    }

    private static boolean isValid(String tc) {

        // 11 hane ve sadece rakam kontrolü
        if (tc == null || !tc.matches("\\d{11}")) {
            return false;
        }

        // İlk hane 0 olamaz
        if (tc.charAt(0) == '0') {
            return false;
        }

        int[] digits = new int[11];
        for (int i = 0; i < 11; i++) {
            digits[i] = tc.charAt(i) - '0';
        }

        // 10. hane kontrolü
        // Tek indeksler (1,3,5,7) * 7, çift indeksler (0,2,4,6,8) * 1 toplamı
        int oddSum  = digits[0] + digits[2] + digits[4] + digits[6] + digits[8];
        int evenSum = digits[1] + digits[3] + digits[5] + digits[7];

        int tenthDigit = ((oddSum * 7) - evenSum) % 10;
        if (tenthDigit < 0) tenthDigit += 10;

        if (digits[9] != tenthDigit) {
            return false;
        }

        // 11. hane kontrolü
        // İlk 10 hanenin toplamının mod 10'u
        int totalSum = 0;
        for (int i = 0; i < 10; i++) {
            totalSum += digits[i];
        }

        int eleventhDigit = totalSum % 10;

        return digits[10] == eleventhDigit;
    }

    public void SifremiUnuttum(SifremiUnuttumRequest loginRequest) {

        User user = userRepository.findByTcAndTelNo(loginRequest.getTc() , loginRequest.getTelNo())
                .orElseThrow(() -> new InvalidTelNoTcException("Tc numarası telefon numarası ıle eslesmiyor"));
        // 6 haneli kod üretme
        String kod = String.valueOf(new Random().nextInt(900000) + 100000);
        user.setKod(kod);
        user.setKodGecerlilikSuresi(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        smsService.smsDogrulamaKoduGonder(loginRequest.getTelNo(), kod);
    }
    private LoginResponse buildResponse(User user) {
        String token = jwtUtil.generateToken(user.getUuid().toString(), user.getRole().name());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRole(user.getRole());
        response.setIlkGiris(user.isIlkGiris());
        return response;
    }
}
