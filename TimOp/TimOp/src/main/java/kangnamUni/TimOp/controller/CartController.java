package kangnamUni.TimOp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import kangnamUni.TimOp.Service.CartService;
import kangnamUni.TimOp.Service.LectureService;
import kangnamUni.TimOp.Service.MemberService;
import kangnamUni.TimOp.domain.Cart;
import kangnamUni.TimOp.domain.CartLecture;
import kangnamUni.TimOp.domain.Lecture;
import kangnamUni.TimOp.domain.Member;
import kangnamUni.TimOp.dto.LectureDTO;
import kangnamUni.TimOp.dto.LectureTimeDTO;
import kangnamUni.TimOp.dto.MemberDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "장바구니", description = "장바구니 관련 API")
@RestController
public class CartController {
    private final MemberService memberService;
    private final CartService cartService;
    private final LectureService lectureService;
    public CartController(MemberService memberService,
                       CartService cartService,
                       LectureService lectureService){
        this.memberService = memberService;
        this.cartService = cartService;
        this.lectureService = lectureService;
    }
    @Transactional
    @GetMapping("/cart/lectures")
    @Operation(
            summary = "장바구니 강의 목록 조회",
            description = "현재 로그인된 사용자의 장바구니에 담긴 강의들을 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LectureDTO.class)))
            }
    )
    public ResponseEntity<?> getCartLectures() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();

        Member member = memberService.findById(memberDetails.getMember().getId()); // 영속 상태
        Cart cart = cartService.findById(member.getCart().getId());                // 영속 상태

        List<CartLecture> cartLectureList = cart.getCartLectures();
        List<Lecture> lectures = new ArrayList<>();
        for (CartLecture cartLecture : cartLectureList) {
            lectures.add(cartLecture.getLecture());
        }
        List<LectureDTO> lectureDTOs = lectures.stream().map(LectureDTO::from).toList();

        return ResponseEntity.ok(lectureDTOs);
    }

    @PostMapping("/cart/lectures/{lectureId}")
    @Transactional
    @Operation(
            summary = "장바구니에 강의 추가",
            description = "특정 강의 ID를 장바구니에 추가합니다. 이미 추가된 경우 400 반환",
            responses = {
                    @ApiResponse(responseCode = "200", description = "추가 성공",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LectureDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "이미 추가된 강의"),
            }
    )
    public ResponseEntity<?> addLectureToCart(@PathVariable("lectureId") Long lectureId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();

        Member member = memberService.findById(memberDetails.getMember().getId());

        Lecture lecture = lectureService.findById(lectureId);
        List<CartLecture> cartLectures = member.getCart().getCartLectures();
        for (CartLecture cartLecture : cartLectures) {
            if(cartLecture.getLecture().equals(lecture)){
                return ResponseEntity.badRequest().build();
            }
        }
        member.getCart().addLecture(lecture);
        List<CartLecture> cartLecturesList = member.getCart().getCartLectures();
        List<Lecture> lectures = new ArrayList<>();
        for (CartLecture cartLecture : cartLecturesList) {
            lectures.add(cartLecture.getLecture());
        }
        List<LectureDTO> lectureDTOs = lectures.stream().map(LectureDTO::from).toList();

        return ResponseEntity.ok(lectureDTOs);
    }
    @DeleteMapping("/cart/lectures/{lectureId}")
    @Transactional
    @Operation(
            summary = "장바구니에서 강의 제거",
            description = "특정 강의 ID를 장바구니에서 제거합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "삭제 성공")
            }
    )
    public ResponseEntity<?> deleteLectureToCart(@PathVariable("lectureId") Long lectureId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();
        Member member = memberService.findById(memberDetails.getMember().getId());
        Lecture lecture = lectureService.findById(lectureId);
        member.getCart().removeLecture(lecture);
        return ResponseEntity.ok().build();
    }
}
