package library_management.com.Repository;

import library_management.com.Entity.KaynakTalepKalem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KaynakTalepKalemRepository extends JpaRepository<KaynakTalepKalem,Long> {
}
