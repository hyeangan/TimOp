package kangnamUni.TimOp.Service;

import jakarta.transaction.Transactional;
import kangnamUni.TimOp.domain.Member;
import kangnamUni.TimOp.domain.SocialAccount;
import kangnamUni.TimOp.dto.GoogleProfileDTO;
import kangnamUni.TimOp.dto.JoinMemberDTO;
import kangnamUni.TimOp.repository.MemberRepository;
import kangnamUni.TimOp.repository.SocialAccountRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SocialAccountRepository socialAccountRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository,
                         BCryptPasswordEncoder bCryptPasswordEncoder,
                         SocialAccountRepository socialAccountRepository){
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder =bCryptPasswordEncoder;
        this.socialAccountRepository = socialAccountRepository;
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
        member.setRole("USER");

        return memberRepository.save(member);
    }
    public Member findById(Long memberId){
        return memberRepository.findById(memberId).orElseThrow();
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
    @Transactional
    public Member upsertFromGoogle(GoogleProfileDTO googleProfileDTO) {
        // 1) 소셜 연결로 먼저 탐색 (provider=google, providerUserId=sub)
        Optional<SocialAccount> linked =
                socialAccountRepository.findByProviderAndProviderMemberId("google", googleProfileDTO.getSub());
        if (linked.isPresent()) {
            // 이미 연결돼 있으면 그 User 반환
            return linked.get().getMember();
        }

        // 2) 연결이 없을 때, 이메일로 기존 User 찾기 (email이 있고, 구글이 검증해줬다고 가정)
        Member member = null;
        if (googleProfileDTO.getEmail() != null && googleProfileDTO.isEmailVerified()) {
            Optional<Member> byEmail = memberRepository.findByEmail(googleProfileDTO.getEmail());
            if (byEmail.isPresent()) {
                member = byEmail.get();
            }
        }

        // 3) 기존 User가 없으면 새로 생성
        if (member == null) {
            member = new Member();
            member.setEmail(googleProfileDTO.getEmail());                // null 허용
            member.setName(googleProfileDTO.getName() != null ? googleProfileDTO.getName() : "Google User");
            member.setRole("ROLE_MEMBER");               // 권한 기본값
            member = memberRepository.save(member);
        }
        // 4) 소셜 연결 레코드 생성
        SocialAccount sa = new SocialAccount();
        sa.setMember(member);
        sa.setProvider("google");
        sa.setProviderMemberId(googleProfileDTO.getSub());
        sa.setEmailFromProvider(googleProfileDTO.getEmail());
        sa.setLinkedAt(Instant.now());
        socialAccountRepository.save(sa);

        return member;
    }
}
