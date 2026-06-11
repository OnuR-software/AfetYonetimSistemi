package AfetYonetimSistemi.Repository;

import AfetYonetimSistemi.Entity.Depo;
import AfetYonetimSistemi.Entity.IL;
import AfetYonetimSistemi.Entity.UserTalep;
import AfetYonetimSistemi.Model.TransferDurumu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTalepRepository extends JpaRepository<UserTalep,Long> {

    List<UserTalep> findByUser_Id(Long id);

    List<UserTalep> findAllByAktifAndMevcutIl(boolean aktif, IL mevcutIl);

    Optional<UserTalep> findByIdAndMevcutIl(Long id, IL mevcutIl);

    List<UserTalep> findAllByDepoAndAktifAndTransferDurumu(Depo depo, boolean aktif, TransferDurumu transferDurumu);

    Optional <UserTalep> findByIdAndDepoAndAktif(Long id, Depo depo, boolean aktif);

    Optional<UserTalep> findByIdAndTransferDurumu(Long id, TransferDurumu transferDurumu);
}
