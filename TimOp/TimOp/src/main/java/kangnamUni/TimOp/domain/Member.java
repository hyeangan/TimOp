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
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true, nullable = false, length =20)
    private String studentId;
    @Column(nullable = false, length =255)
    private String password;
    @Column(nullable = false, length=20)
    private String name;
    @Column(nullable = false, length =50)
    private String major;
    @Column(nullable = false, length=20)
    private String role = "USER";

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true) //mappedBy = "member" -> Timetable 에 member필드가 관계의 주인(외래키)
    @JsonManagedReference
    private List<Timetable> timetables = new ArrayList<>();

    @PrePersist
    private void initCart() {
        if (cart == null) {
            cart = new Cart(this);
        }
    }

    public void addTimetable(Timetable timetable){
        timetables.add(timetable);
        timetable.setMember(this);
    }
    public void removeTimetable(Timetable timetable){
        timetables.remove(timetable);
        timetable.setMember(null);
    }
}
