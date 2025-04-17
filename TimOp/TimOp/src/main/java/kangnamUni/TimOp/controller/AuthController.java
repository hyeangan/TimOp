package kangnamUni.TimOp.controller;

import kangnamUni.TimOp.Service.MemberService;
import kangnamUni.TimOp.domain.Member;
import kangnamUni.TimOp.dto.JoinMemberDTO;
import kangnamUni.TimOp.dto.JoinResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final MemberService memberService;
    @Autowired
    public AuthController(MemberService memberService){
        this.memberService = memberService;
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody JoinMemberDTO joinMemberDTO){
        try {
            Member member = memberService.join(joinMemberDTO);
            JoinResponseDTO joinMember = new JoinResponseDTO();
            joinMember.setStudentId(member.getStudentId());
            return ResponseEntity.status(HttpStatus.CREATED).body(joinMember);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());

        }

    }
    @PostMapping("/member")
    public String admin(){
        return "member controller";
    }
}
