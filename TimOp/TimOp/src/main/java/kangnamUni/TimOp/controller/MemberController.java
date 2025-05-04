package kangnamUni.TimOp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kangnamUni.TimOp.dto.MemberDTO;
import kangnamUni.TimOp.dto.MemberDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원", description = "로그인 회원 관련 API")
@RestController
public class MemberController {
    @Operation(
            summary = "로그인 사용자 정보 조회",
            description = "로그인한 사용자의 정보를 조회해서 정보 전달",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용자 정보 전달")
            }
    )
    @GetMapping("members/me")
    public ResponseEntity<?> getLoginMemberProfile(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();
        MemberDTO memberDTO = new MemberDTO(memberDetails.getMember().getName());
        return ResponseEntity.ok(memberDTO);
    }
}
