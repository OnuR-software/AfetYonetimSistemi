package library_management.com.Repository;

import library_management.com.Entity.UserTalepKalem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTalepKalemRepository extends JpaRepository<UserTalepKalem,Long> {
}
