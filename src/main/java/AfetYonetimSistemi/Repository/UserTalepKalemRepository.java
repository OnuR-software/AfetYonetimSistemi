package AfetYonetimSistemi.Repository;

import AfetYonetimSistemi.Entity.UserTalepKalem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTalepKalemRepository extends JpaRepository<UserTalepKalem,Long> {
}
