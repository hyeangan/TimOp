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

    //다대다 lecture와 timetable 중간엔티티가 의미가없어서 manytomany로 간단히 구현 단방향
    @ManyToMany
    @JoinTable(
            name = "timetable_lecture",  // 중간 테이블 이름
            joinColumns = @JoinColumn(name = "timetable_id"),  // Timetable이 주인
            inverseJoinColumns = @JoinColumn(name = "lecture_id") // Lecture 연결
    )
    private List<Lecture> lectures = new ArrayList<>();

    private String name;
}