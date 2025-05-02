package kangnamUni.TimOp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "lecture_times")
public class LectureTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20)
    private String time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private DayOfWeekEnum dayOfWeek; // "월", "화", "수", "목", "금"

    private LocalTime startTime;  // 시작 시간 (09:00)
    private LocalTime endTime;    // 종료 시간 (10:15)

}