package library_management.com.Repository;

import library_management.com.Entity.Koordinator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KoordinatorRepository extends JpaRepository<Koordinator,Long> {

}
