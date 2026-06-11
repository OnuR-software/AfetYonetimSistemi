package AfetYonetimSistemi.Repository;

import AfetYonetimSistemi.Entity.Depo;
import AfetYonetimSistemi.Entity.DepoMalzeme;
import AfetYonetimSistemi.Entity.Malzeme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepoMalzemeRepository extends JpaRepository<DepoMalzeme,Long> {
    Optional<DepoMalzeme> findByDepoAndMalzeme(Depo depo, Malzeme malzeme);
}
