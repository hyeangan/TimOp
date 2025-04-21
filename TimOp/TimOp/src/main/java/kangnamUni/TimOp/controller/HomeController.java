package kangnamUni.TimOp.controller;

import jakarta.servlet.http.HttpSession;
import kangnamUni.TimOp.Service.LectureService;
import kangnamUni.TimOp.Service.MemberService;
import kangnamUni.TimOp.Service.TimetableService;
import kangnamUni.TimOp.domain.*;
import kangnamUni.TimOp.dto.*;
import kangnamUni.TimOp.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@Slf4j
public class HomeController {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberService memberService;

    @Autowired
    private LectureService lectureService;
    @Autowired
    private TimetableService timetableService;

    @GetMapping("/home")
    public String home(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            /*
            MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();
            Member member = memberDetails.getMember();
            model.addAttribute("member", member);
            List<Timetable> timetables = timetableService.findByMemberId(member.getId());
             */
            return "home";
        }
        return"home";
    }

    @GetMapping("/")
    public String frontPage(){
        return "front";
    }

    //section1 tab2에 시간표에 저장된 강의 리스트 보내기
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
    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }
    @GetMapping("/join")
    public String joinPage(){
        return "join";
    }
    //DB 시간표에 강의 저장
    @PostMapping("/timetables/lectures")
    @ResponseBody
    public ResponseEntity<TimetableDTO> addLecture(@RequestParam("id") Long lectureId, @RequestParam("timetableName") String timetableName, Model model, HttpSession session) {
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
    @DeleteMapping("/timetables/{timetableName}/lectures/{lectureId}")
    @ResponseBody
    public ResponseEntity<TimetableDTO> deleteTimetableLecture(@PathVariable("timetableName") String timetableName, @PathVariable("lectureId") Long lectureId, HttpSession session){
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
    //시간표 표시
    @GetMapping("/timetables/{timetableName}")
    @ResponseBody
    public ResponseEntity<TimetableDTO> getTimetable(@PathVariable("timetableName") String timetableName) {

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
        // 🔴 인증 실패: 403 Forbidden
        return ResponseEntity.status(403).build();
    }
    @GetMapping("/timetables")
    @ResponseBody
    public ResponseEntity<List<TimetableDTO>> getAllTimetables() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof MemberDetails memberDetails) {
            return ResponseEntity.ok(timetableService.getAllTimetablesInfo());

        }
        return ResponseEntity.status(403).build();
    }

    //시간표 추가
    @PostMapping("/timetables")
    public ResponseEntity<?> addTimetable(@RequestBody TimetableRequestDTO request, Model model, HttpSession session) {

        String timetableName = request.getName();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();
        Member member = memberDetails.getMember();

        if(timetableService.existsByName(timetableName, member.getId())){
            //예외처리 알림 추가해
            model.addAttribute("member", member);
            List<Timetable> timetables = timetableService.findByMemberId(member.getId());

            return ResponseEntity.badRequest().build();
        }
        Timetable newTimetable = timetableService.createTimetableForMember(member.getId(), timetableName);

        return ResponseEntity.ok().body(newTimetable);
    }
    @DeleteMapping("/timetables/{name}")
    public ResponseEntity<?> deleteTimetable(@PathVariable("name") String name) {
        log.info("삭제 시간표 이름" + name);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();
        Member member = memberDetails.getMember();

        timetableService.deleteTimetableForMember(member.getId(), name);

        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }

    private static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
