package library_management.com.Repository;

import library_management.com.Entity.Depo;
import library_management.com.Entity.DepoMalzeme;
import library_management.com.Entity.Malzeme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepoMalzemeRepository extends JpaRepository<DepoMalzeme,Long> {
    Optional<DepoMalzeme> findByDepoAndMalzeme(Depo depo, Malzeme malzeme);
}
