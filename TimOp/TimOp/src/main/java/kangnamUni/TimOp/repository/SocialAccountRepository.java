package kangnamUni.TimOp.repository;

import kangnamUni.TimOp.domain.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    Optional<SocialAccount> findByProviderAndProviderMemberId(String provider, String providerMemberId);
}
