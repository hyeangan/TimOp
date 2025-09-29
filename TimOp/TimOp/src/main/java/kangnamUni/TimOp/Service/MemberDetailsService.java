package kangnamUni.TimOp.Service;

import kangnamUni.TimOp.domain.Member;
import kangnamUni.TimOp.dto.MemberDetails;
import kangnamUni.TimOp.repository.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    public MemberDetailsService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String pkId) throws UsernameNotFoundException {
        Long memberId;
        try {
            memberId = Long.valueOf(pkId);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid user id format: " + pkId);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + pkId));

        return new MemberDetails(member);
    }
}
