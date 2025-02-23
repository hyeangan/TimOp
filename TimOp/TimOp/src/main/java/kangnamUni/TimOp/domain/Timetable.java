package kangnamUni.TimOp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Timetable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @JsonBackReference
    private Member member;

    @OneToMany(mappedBy = "timetable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lecture> lectures = new ArrayList<>();;

    private String name;
    public void addLecture(Lecture lecture) {
        lectures.add(lecture);
        lecture.setTimetable(this); // Lecture 엔티티의 timetable 설정 (양방향 관계 유지)
    }

    // 강의 제거 메서드 (삭제 가능)
    public void removeLecture(Lecture lecture) {
        lectures.remove(lecture);
        lecture.setTimetable(null);
    }
}