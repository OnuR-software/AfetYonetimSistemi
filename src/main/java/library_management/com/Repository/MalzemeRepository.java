package library_management.com.Repository;

import library_management.com.Entity.Malzeme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MalzemeRepository extends JpaRepository<Malzeme,Long> {
}
