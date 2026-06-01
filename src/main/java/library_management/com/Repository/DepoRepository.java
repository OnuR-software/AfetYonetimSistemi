package library_management.com.Repository;

import library_management.com.Entity.Depo;
import library_management.com.Entity.IL;
import library_management.com.Model.DepoModeli;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepoRepository extends JpaRepository<Depo,Long> {

        List<Depo> findByIl_Id(Long id);

    Optional<Depo> findByIdAndIl(Long id, IL il);

    List<Depo> findAllByIlAndAktif(IL il, boolean aktif);

    List<Depo> findAllByAktifAndIlAndDepoModeli(boolean aktif, IL il, DepoModeli depoModeli);

    Optional<Depo> findByIdAndDepoModeliAndIl(Long id, DepoModeli depoModeli, IL il);

    List<Depo> findAllByAktif(boolean aktif);

    List<Depo> findAllByAktifAndDepoModeliIn(boolean aktif, Collection<DepoModeli> depoModelis);
}
