package kangnamUni.TimOp.repository;

import kangnamUni.TimOp.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//JpaRepository로 기본 CRUD 상속 / 복잡한 검색을 위한 LectureRepositoryCustom
@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long>,  LectureRepositoryCustom{
    List<Lecture> findByTitleContaining(String title);
}
