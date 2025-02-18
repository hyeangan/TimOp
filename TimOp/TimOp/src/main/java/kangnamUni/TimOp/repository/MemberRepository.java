package kangnamUni.TimOp.repository;

import kangnamUni.TimOp.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByStudentId(String studentId);
    boolean existsByStudentId(String studentId);
}
