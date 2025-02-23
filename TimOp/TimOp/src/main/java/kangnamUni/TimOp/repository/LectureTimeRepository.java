package kangnamUni.TimOp.repository;

import kangnamUni.TimOp.domain.LectureTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureTimeRepository extends JpaRepository<LectureTime, Long> {
}
