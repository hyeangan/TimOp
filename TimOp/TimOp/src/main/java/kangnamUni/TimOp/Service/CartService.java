package kangnamUni.TimOp.Service;

import kangnamUni.TimOp.domain.Cart;
import kangnamUni.TimOp.repository.CartRepository;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    private final CartRepository cartRepository;
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }
    public Cart findById(Long cartId){
        return cartRepository.findById(cartId).orElseThrow();
    }

}
