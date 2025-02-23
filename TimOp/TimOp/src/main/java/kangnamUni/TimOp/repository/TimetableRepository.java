package kangnamUni.TimOp.repository;

import kangnamUni.TimOp.domain.Member;
import kangnamUni.TimOp.domain.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    boolean existsByMemberAndName(Member member, String name);
    Optional<Timetable> findByNameAndMember(String name, Member member);

    List<Timetable> findByMember(Member member);

}
