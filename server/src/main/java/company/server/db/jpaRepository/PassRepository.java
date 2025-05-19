package company.server.db.jpaRepository;

import company.server.db.entity.Pass;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassRepository extends JpaRepository<Pass, Long> {
    List<Pass> findByCode(Long code);
}
