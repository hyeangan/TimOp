package kangnamUni.TimOp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Slf4j
public class Lecture {
    //학수번호 분반 과목명 담당교수 학점 시수 강의시간
    //ND01603 12 컴퓨터프로그래밍 백남진 3 3 (주)목1ab2ab3ab
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String num; //학수번호
    private String division_class;
    private String title;
    private String professor;
    private int credit; //학점
    private String progress_time; //시수
    //private String time; 스크래퍼 수정
    private String syllabus;
    private String major;
    private int grade;
    private int year;
    private String semester;
    private String liberalArts;

    @ManyToOne()
    @JoinColumn(name = "timetable_id")
    private Timetable timetable;
    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureTime> lectureTimes = new ArrayList<>(); // 여러 개의 시간 블록을 가질 수 있음
    public Lecture() {
    }
    public void addLectureTime(LectureTime lectureTime){
        lectureTimes.add(lectureTime);
        lectureTime.setLecture(this);
    }
    public void removeLectureTime(LectureTime lectureTime) {
        lectureTimes.remove(lectureTime);
        lectureTime.setLecture(null); // 관계 해제
    }
}
