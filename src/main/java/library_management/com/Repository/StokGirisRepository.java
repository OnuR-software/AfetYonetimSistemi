package library_management.com.Repository;

import library_management.com.Entity.StokGiris;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StokGirisRepository extends JpaRepository<StokGiris,Long> {

}
