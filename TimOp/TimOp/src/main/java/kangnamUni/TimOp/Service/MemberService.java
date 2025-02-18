package kangnamUni.TimOp.Service;

import kangnamUni.TimOp.domain.Member;
import kangnamUni.TimOp.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;

    public boolean checkStudentIdDuplicate(String studentId) {
        return memberRepository.existsByStudentId(studentId);
    }
    public Member login(String studentId, String password) {
        Member member = memberRepository.findByStudentId(studentId);
        if (member != null && member.getPassword().equals(password)) {
            return member;
        }
        return null;
    }
}
