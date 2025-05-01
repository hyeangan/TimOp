package kangnamUni.TimOp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
@Getter
@Setter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartLecture> cartLectures = new ArrayList<>();

    protected Cart() {}
    public Cart(Member member) {
        this.member = member;
    }

    // 아이템 추가
    public void addLecture(Lecture lecture) {
        CartLecture cartLecture = new CartLecture(lecture, this);
        cartLectures.add(cartLecture);
        lecture.getCartLectures().add(cartLecture);
    }

    // 아이템 제거 for-each 반복중 컬렉션 변경금지
    public void removeLecture(Lecture lecture) {
        Iterator<CartLecture> iterator = cartLectures.iterator();
        while (iterator.hasNext()) {
            CartLecture cartLecture = iterator.next();
            if (cartLecture.getLecture().equals(lecture)) {
                ((Iterator<?>) iterator).remove(); // 안전하게 리스트에서 제거
                lecture.getCartLectures().remove(cartLecture);
                cartLecture.setCart(null);     // 양방향 관계 끊기
                cartLecture.setLecture(null);  // 필요 시
            }
        }
    }
}
