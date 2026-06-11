package AfetYonetimSistemi.Repository;

import AfetYonetimSistemi.Entity.Afet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AfetRepository extends JpaRepository<Afet,Long> {
}
