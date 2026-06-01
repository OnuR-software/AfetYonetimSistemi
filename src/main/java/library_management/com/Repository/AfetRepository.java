package library_management.com.Repository;

import library_management.com.Entity.Afet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AfetRepository extends JpaRepository<Afet,Long> {
}
