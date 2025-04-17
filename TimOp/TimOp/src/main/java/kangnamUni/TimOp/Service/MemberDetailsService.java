package kangnamUni.TimOp.Service;

import kangnamUni.TimOp.domain.Member;
import kangnamUni.TimOp.dto.MemberDetails;
import kangnamUni.TimOp.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MemberDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    public MemberDetailsService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String studentId) throws UsernameNotFoundException {
        Member member = memberRepository.findByStudentId(studentId);
        if(member != null){
            return new MemberDetails(member);
        }
        return null;
    }
}
