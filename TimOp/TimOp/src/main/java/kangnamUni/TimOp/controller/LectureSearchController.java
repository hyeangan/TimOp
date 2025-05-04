package kangnamUni.TimOp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kangnamUni.TimOp.Service.LectureService;
import kangnamUni.TimOp.Service.TimetableService;
import kangnamUni.TimOp.domain.*;
import kangnamUni.TimOp.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "검색", description = "강의 필터린 관련 API")
@Slf4j
@RestController
public class LectureSearchController {
    private final LectureService lectureService;
    private final TimetableService timetableService;

    @Autowired
    public LectureSearchController(LectureService lectureService, TimetableService timetableService) {
        this.lectureService = lectureService;
        this.timetableService = timetableService;
    }
    @Operation(
            summary = "강의명 검색",
            description = "입력된 키워드를 포함하는 강의명을 검색합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "검색 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 실패")
            }
    )
    @GetMapping("/lectures/search")
    public ResponseEntity<List<LectureDTO>> searchByTitle(@Parameter(description = "검색할 강의 키워드", example = "컴퓨") @RequestParam("title") String title) {
        log.info("searchByTitle");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof MemberDetails memberDetails) {
            Member member = memberDetails.getMember();

            List<Lecture> lectures = lectureService.findLecturesByTitleContaining(title);
            List<LectureDTO> lectureDTOs = lectures.stream().map(LectureDTO::from).toList();

            return ResponseEntity.ok(lectureDTOs);
        }
        // 인증 실패: 403 Forbidden
        return ResponseEntity.status(403).build();
    }

    //처음 @RequestParam 으로 모든 경우의 수에 대해 만들다 -> 변경
    @Operation(
            summary = "조건별 강의 검색",
            description = "다양한 조건(title, professor, 요일, 시간 등)을 이용해 강의를 검색합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "검색 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 실패")
            }
    )
    @GetMapping("lectures/search/condition")
    public ResponseEntity<List<LectureDTO>> searchByCondition(@Parameter(description = "검색 조건 DTO") @ModelAttribute LectureFilterDTO filterDTO) {
        //시간대 enum custom, empty 구분

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof MemberDetails memberDetails) {
            Member member = memberDetails.getMember();

            List<Lecture> lectures = lectureService.findLectures(filterDTO);
            List<LectureDTO> lectureDTOs = lectures.stream().map(LectureDTO::from).toList();

            List<Timetable> timetables = timetableService.findByMemberId(member.getId());

            return ResponseEntity.ok(lectureDTOs);
        }
        // 인증 실패: 403 Forbidden
        return ResponseEntity.status(403).build();

    }
}
