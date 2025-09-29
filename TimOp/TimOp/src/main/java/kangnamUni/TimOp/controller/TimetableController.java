package kangnamUni.TimOp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import kangnamUni.TimOp.Service.MemberService;
import kangnamUni.TimOp.Service.TimetableService;
import kangnamUni.TimOp.domain.Member;
import kangnamUni.TimOp.domain.Timetable;
import kangnamUni.TimOp.dto.LectureDTO;
import kangnamUni.TimOp.dto.MemberDetails;
import kangnamUni.TimOp.dto.TimetableDTO;
import kangnamUni.TimOp.dto.TimetableRequestDTO;
import kangnamUni.TimOp.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "시간표", description = "시간표 관리 API")
@RestController
@Slf4j
@SecurityRequirement(name = "JWT")
public class TimetableController {
    private final TimetableService timetableService;
    private final MemberService memberService;
    public TimetableController(TimetableService timetableService,
                               MemberService memberService){
        this.timetableService = timetableService;
        this.memberService = memberService;
    }
    @Operation(summary = "시간표 전체 조회", description = "로그인된 사용자의 모든 시간표를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "인증 실패")
    })
    @GetMapping("/timetables")
    public ResponseEntity<List<TimetableDTO>> getAllTimetables() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof MemberDetails memberDetails) {
            Long memberId = memberDetails.getMember().getId();
            Member member = memberService.findById(memberId);
            try{
                List<Timetable> timetables = member.getTimetables();
                List<TimetableDTO> timetableDTOs = new ArrayList<>();
                for (Timetable timetable : timetables) {
                    timetableDTOs.add(timetableService.convertTimetableDTO(timetable));
                }
                return ResponseEntity.ok(timetableDTOs);
            }catch(Exception e) {
                log.info(e.getMessage());
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.status(403).build();
    }
    @Operation(summary = "특정 시간표 조회", description = "시간표 이름을 통해 해당 시간표를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 이름의 시간표 없음"),
            @ApiResponse(responseCode = "403", description = "인증 실패")
    })
    @GetMapping("/timetables/{timetableName}")
    public ResponseEntity<TimetableDTO> getTimetable(@Parameter(description = "조회할 시간표 이름", example = "시간표1")
                                                    @PathVariable("timetableName") String timetableName) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof MemberDetails memberDetails) {
            Member member = memberDetails.getMember();

            Timetable timetable = timetableService.findByNameAndMember(timetableName, member);
            if (timetable == null) {
                return ResponseEntity.notFound().build(); // 시간표 없음
            }
            TimetableDTO timetableDTO = timetableService.convertTimetableDTO(timetable);
            return ResponseEntity.ok(timetableDTO);
        }
        return ResponseEntity.status(403).build();
    }
    @Operation(summary = "시간표 생성", description = "시간표 이름을 받아 새 시간표를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "이미 존재하는 시간표 이름"),
            @ApiResponse(responseCode = "403", description = "인증 실패")
    })
    @PostMapping("/timetables")
    @Transactional
    public ResponseEntity<?> addTimetable(@RequestBody TimetableRequestDTO request) {

        String timetableName = request.getName();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();
        Member member = memberDetails.getMember();

        if(timetableService.existsByName(timetableName, member.getId())){
            //예외처리 알림 추가해
            List<Timetable> timetables = timetableService.findByMemberId(member.getId());

            return ResponseEntity.badRequest().build();
        }
        Timetable newTimetable = timetableService.createTimetableForMember(member.getId(), timetableName);

        return ResponseEntity.ok().body(newTimetable);
    }
    @Operation(summary = "시간표에 강의 추가", description = "시간표에 특정 강의 추가합니다.")
    @PostMapping("/timetables/{timetableName}/lectures/{id}")
    public ResponseEntity<TimetableDTO> addLectureToTimetable(@PathVariable("id") Long lectureId, @PathVariable("timetableName") String timetableName) {
        // lectureId를 사용하여 필요한 작업 수행
        // 예: lectureId를 사용하여 데이터베이스에서 강의 정보 조회 및 처리
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof MemberDetails memberDetails) {
            Member member = memberDetails.getMember();
            timetableService.addLectureToTimetable(member.getId(), timetableName, lectureId);
            Timetable timetable = timetableService.findByNameAndMember(timetableName, member);
            TimetableDTO timetableDTO = timetableService.convertTimetableDTO(timetable);
            return ResponseEntity.ok(timetableDTO);
        }
        return ResponseEntity.status(403).build();
    }
    @Operation(summary = "시간표 강의 목록 조회", description = "특정 시간표에 포함된 강의들을 조회합니다.")
    @GetMapping("/timetables/{timetableName}/lectures")
    public ResponseEntity<List<LectureDTO>> getTimetableLectures(
            @PathVariable("timetableName") String timetableName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof MemberDetails memberDetails) {
            Member member = memberDetails.getMember();
            Timetable timetable = timetableService.findByNameAndMember(timetableName, member);
            if (timetable == null) {
                return ResponseEntity.notFound().build(); // 시간표 없음
            }
            List<LectureDTO> lectureDTOs = timetableService.convertTimetableLectureDTOs(timetable);
            return ResponseEntity.ok(lectureDTOs);
        }
        // 🔴 인증 실패: 403 Forbidden
        return ResponseEntity.status(403).build();
    }
    @Operation(summary = "시간표 삭제", description = "특정 시간표를 삭제합니다.")
    @DeleteMapping("/timetables/{name}")
    public ResponseEntity<?> deleteTimetable(@PathVariable("name") String name) {
        log.info("삭제 시간표 이름" + name);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();
        Member member = memberDetails.getMember();

        timetableService.deleteTimetableForMember(member.getId(), name);

        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }
    @Operation(summary = "시간표에서 강의 제거", description = "특정 시간표에서 강의를 제거합니다.")
    @DeleteMapping("/timetables/{timetableName}/lectures/{lectureId}")
    public ResponseEntity<TimetableDTO> removeLectureFromTimetable(@PathVariable("timetableName") String timetableName, @PathVariable("lectureId") Long lectureId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof MemberDetails memberDetails) {
            Member member = memberDetails.getMember();

            Timetable timetable = timetableService.deleteTimetableLecture(member.getId(), timetableName, lectureId);
            TimetableDTO timetableDTO = timetableService.convertTimetableDTO(timetable);
            return ResponseEntity.ok(timetableDTO);
        }
        return ResponseEntity.status(403).build();


    }
}
