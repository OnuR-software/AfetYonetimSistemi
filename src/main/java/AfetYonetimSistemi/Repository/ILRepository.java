package AfetYonetimSistemi.Repository;

import AfetYonetimSistemi.Entity.IL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ILRepository extends JpaRepository<IL,Long> {

    Optional<IL> findByIlAdi(String ilAdi);
}
