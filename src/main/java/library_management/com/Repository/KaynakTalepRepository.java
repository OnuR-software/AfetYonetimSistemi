package library_management.com.Repository;

import library_management.com.Entity.Depo;
import library_management.com.Entity.KaynakTalep;
import library_management.com.Model.TransferDurumu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KaynakTalepRepository extends JpaRepository<KaynakTalep,Long> {
    List<KaynakTalep> findByKoordinator_Id(Long koordinatorİd);

    List<KaynakTalep> findByKoordinator_IdAndAktif(Long koordinatorİd, boolean aktif);

    Optional<KaynakTalep> findByIdAndTransferDurumu(Long id, TransferDurumu transferDurumu);

    List<KaynakTalep> findAllByAktifAndTransferDurumu(boolean aktif, TransferDurumu transferDurumu);

    List<KaynakTalep> findByYardımGoturenDepoAndAktif(Depo yardımGoturenDepo, boolean aktif);

    List<KaynakTalep> findByYardımGoturenDepoAndAktifAndTransferDurumu(Depo yardımGoturenDepo, boolean aktif, TransferDurumu transferDurumu);

    Optional<KaynakTalep> findByIdAndTransferDurumuAndYardımGoturenDepo(Long id, TransferDurumu transferDurumu, Depo yardımGoturenDepo);
}
