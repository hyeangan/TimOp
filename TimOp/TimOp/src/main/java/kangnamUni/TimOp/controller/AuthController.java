package kangnamUni.TimOp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kangnamUni.TimOp.Service.MemberService;
import kangnamUni.TimOp.domain.Member;
import kangnamUni.TimOp.dto.JoinMemberDTO;
import kangnamUni.TimOp.dto.JoinResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증/인가", description = "인증 인가 실행 API")
@RestController
public class AuthController {
    private final MemberService memberService;
    @Autowired
    public AuthController(MemberService memberService){
        this.memberService = memberService;
    }

    @Operation(
            summary = "회원가입",
            description = "학번, 비밀번호, 이름, 전공을 받아 새로운 사용자를 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "회원가입 성공",
                            content = @Content(schema = @Schema(implementation = JoinResponseDTO.class),
                                    mediaType = "application/json"
                            )),
                    @ApiResponse(responseCode = "400", description = "이미 존재하는 학번 또는 잘못된 요청")
            }
    )

    @PostMapping("/join")
    public ResponseEntity<?> join( @Parameter(description = "회원가입 정보", required = true) @RequestBody JoinMemberDTO joinMemberDTO){
        try {
            Member member = memberService.join(joinMemberDTO);
            JoinResponseDTO joinMember = new JoinResponseDTO();
            joinMember.setStudentId(member.getStudentId());
            return ResponseEntity.status(HttpStatus.CREATED).body(joinMember);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body("회원가입 실패" + e.getMessage());

        }
    }
}
