package kangnamUni.TimOp.Service;

import kangnamUni.TimOp.domain.Member;
import kangnamUni.TimOp.dto.JoinMemberDTO;
import kangnamUni.TimOp.repository.MemberRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository,
                         BCryptPasswordEncoder bCryptPasswordEncoder){
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder =bCryptPasswordEncoder;
    }
    public Member join(JoinMemberDTO joinMemberDTO){
        if(checkStudentIdDuplicate(joinMemberDTO.getStudentId())){
           throw new IllegalArgumentException("사용자 ID 중복입니다.");
        }
        Member member = new Member();
        member.setStudentId(joinMemberDTO.getStudentId());
        member.setPassword(bCryptPasswordEncoder.encode(joinMemberDTO.getPassword()));
        member.setName(joinMemberDTO.getName());
        member.setMajor(joinMemberDTO.getMajor());
        member.setRole("ROLE_MEMBER");

        return memberRepository.save(member);
    }

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
