package kangnamUni.TimOp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lectures")
@Getter
@Setter
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String num; // 학수번호

    @Column(name = "division_class", nullable = false, length = 10)
    private String divisionClass; // 분반

    @Column(nullable = false, length = 80)
    private String title; // 과목명

    @Column(length = 20)
    private String professor;

    @Column(nullable = false)
    private int credit; //학점

    @Column(nullable = false, length = 20)
    private String progressTime; // 시수 표현 문자열

    @Column(length = 100)
    private String syllabus; // syllabus 파일명 or URL

    @Column(length = 50)
    private String major;

    @Column(length = 20)
    private int grade;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false, length = 20)
    private String semester; // ex: 1학기, 2학기

    @Column(length = 30)
    private String liberalArts; // 교양 구분 (선택)

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartLecture> cartLectures = new ArrayList<>();

    @ManyToMany(mappedBy = "lectures")
    private List<Timetable> timetables = new ArrayList<>();

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureTime> lectureTimes = new ArrayList<>();

    public void addLectureTime(LectureTime lectureTime){
        lectureTimes.add(lectureTime);
        lectureTime.setLecture(this);
    }

    public void removeLectureTime(LectureTime lectureTime) {
        lectureTimes.remove(lectureTime);
        lectureTime.setLecture(null);
    }
}
