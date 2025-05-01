package kangnamUni.TimOp.repository;

import kangnamUni.TimOp.domain.Cart;
import kangnamUni.TimOp.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
}
