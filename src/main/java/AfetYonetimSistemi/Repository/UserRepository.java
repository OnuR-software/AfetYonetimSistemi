package AfetYonetimSistemi.Repository;

import AfetYonetimSistemi.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User findByTc(String tc);

    Boolean existsByTc(String tc);

    Optional<User> findByUuid(UUID uuid);

    Optional <User> findByTcAndTelNo(String tc, String telNo);
}
