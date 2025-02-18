package kangnamUni.TimOp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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
    private String time;
    private String syllabus;
    private String major;
    private int grade;
    private int year;
    private String semester;
    private String liberalArts;

    @ManyToOne()
    @JoinColumn(name = "timetable_id")
    private Timetable timetable;

    public Lecture() {
    }

}
