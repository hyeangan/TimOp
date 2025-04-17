package kangnamUni.TimOp.Service;

import jakarta.transaction.Transactional;
import kangnamUni.TimOp.domain.Lecture;
//import kangnamUni.TimOp.repository.LectureRepository;
import kangnamUni.TimOp.dto.LectureFilterDTO;
import kangnamUni.TimOp.repository.LectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LectureService {
    /*
    private final EntityManagerFactory entityManagerFactory;

    @Autowired //? 시발?
    public LectureService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;

    }
    //@Transactional
    public void lectureToDatabase(Lecture lecture) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(lecture);
        // Perform custom operations
        transaction.commit();
        em.close();
    }

     */
    private final LectureRepository lectureRepository;
    @Autowired
    public LectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository; // 생성자를 통한 의존성 주입
    }
    @Transactional
    public void lectureToDatabase(Lecture lecture) {
        lectureRepository.save(lecture);
    }

    //Spring Data JPA
    /*
    findBy 쿼리 생성 엔티티 지정 title(entity) Containting -> Sql 에 LIKE -> 키워드 포함
    SELECT * FROM course WHERE title LIKE %?1%; -> ?1은 메서드 호출시 첫번쨰 인수
    */
    @Transactional
    public List<Lecture> findLecturesByTitleContaining(String title) {
        return lectureRepository.findByTitleContaining(title);
    }
    @Transactional  //필요한가?
    public List<Lecture> findLectures(LectureFilterDTO filterDTO) {
        return lectureRepository.findLectures(filterDTO);
    }

    public Lecture findById(Long lectureId) {
        Optional<Lecture> lecture = lectureRepository.findById(lectureId);
        return lecture.orElseThrow(() -> new RuntimeException("Lecture not found with id: " + lectureId));
    }

}
