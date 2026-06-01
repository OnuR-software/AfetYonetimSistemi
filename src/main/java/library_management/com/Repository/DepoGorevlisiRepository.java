package library_management.com.Repository;

import library_management.com.Entity.Depo;
import library_management.com.Entity.DepoGorevlisi;
import library_management.com.Entity.IL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepoGorevlisiRepository extends JpaRepository<DepoGorevlisi,Long> {

    List<DepoGorevlisi> findAllByGorevYaptıgıIl(IL gorevYaptıgıIl);

    List<DepoGorevlisi> findAllByDepoAndGorevYaptıgıIl(Depo depo, IL gorevYaptıgıIl);

    Optional<DepoGorevlisi> findByIdAndDepo(Long id, Depo depo);
}
