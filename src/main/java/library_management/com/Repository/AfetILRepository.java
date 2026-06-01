package library_management.com.Repository;

import library_management.com.Entity.AfetIL;
import library_management.com.Entity.IL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AfetILRepository extends JpaRepository<AfetIL,Long> {

    List<AfetIL> findAllByAktifAndIl(boolean aktif, IL il);

    List<AfetIL> findAllByAktif(boolean aktif);

    Optional<AfetIL> findByIdAndAktif(Long id , boolean aktif);
}
