package kangnamUni.TimOp.domain;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Slf4j
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
    private String studentId;
    private String password;
    private String name;
    private String major;
    private String role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true) //mappedBy = "member" -> Timetable 에 member필드가 관계의 주인(외래키)
    @JsonManagedReference
    private List<Timetable> timetables = new ArrayList<>();
    public void addTimetable(Timetable timetable){
        timetables.add(timetable);
        timetable.setMember(this);
    }
    public void removeTimetable(Timetable timetable){
        timetables.remove(timetable);
        timetable.setMember(null);
    }
}
