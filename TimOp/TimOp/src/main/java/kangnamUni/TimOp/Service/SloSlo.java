package kangnamUni.TimOp.Service;

import kangnamUni.TimOp.domain.Lecture;
import kangnamUni.TimOp.domain.LectureTime;
import kangnamUni.TimOp.repository.LectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class SloSlo {

    private final LectureRepository lectureRepository;
    @Autowired
    public SloSlo(LectureRepository lectureRepository){
        this.lectureRepository = lectureRepository;
        test2();
        System.out.println("dd");
    }
    public void test(){
        Lecture lecture = new Lecture();
        lecture.setTitle("최형안");
        lectureRepository.save(lecture);
        System.out.println("gg");
    }
    public void test2(){
        Lecture lecture = new Lecture();
        lecture.setTitle("최종");
        LectureTime lectureTime = new LectureTime();
        lectureTime.setStartTime(LocalTime.of(9,0));
        lectureTime.setEndTime(LocalTime.of(10,10));
        lecture.addLectureTime(lectureTime);
        LectureTime lectureTime2 = new LectureTime();
        lectureTime2.setStartTime(LocalTime.of(13,15));
        lectureTime2.setEndTime(LocalTime.of(16,58));
        lecture.addLectureTime(lectureTime2);

        lectureRepository.save(lecture);

    }
}


