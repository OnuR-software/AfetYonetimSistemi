package AfetYonetimSistemi.Repository;

import AfetYonetimSistemi.Entity.Malzeme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MalzemeRepository extends JpaRepository<Malzeme,Long> {
}
