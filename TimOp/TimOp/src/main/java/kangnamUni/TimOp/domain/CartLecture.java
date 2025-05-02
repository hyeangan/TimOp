package kangnamUni.TimOp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "cart_lectures",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "lecture_id"})
)
@Getter
@Setter
public class CartLecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;
    protected CartLecture() {}
    public CartLecture(Lecture lecture, Cart cart){
        this.lecture = lecture;
        this.cart = cart;
    }
}
