package AfetYonetimSistemi.Service;

import AfetYonetimSistemi.Entity.User;
import AfetYonetimSistemi.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UUID id = UUID.fromString(username);
        User user = userRepository.findByUuid(id)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı"));
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(id.toString())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
