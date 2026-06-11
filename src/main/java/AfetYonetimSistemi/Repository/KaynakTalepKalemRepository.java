package AfetYonetimSistemi.Repository;

import AfetYonetimSistemi.Entity.KaynakTalepKalem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KaynakTalepKalemRepository extends JpaRepository<KaynakTalepKalem,Long> {
}
