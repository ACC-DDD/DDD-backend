package acc.firewatch.cctv.Repository;

import acc.firewatch.cctv.entity.Cctv;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CctvRepository extends JpaRepository<Cctv, Long> {
}