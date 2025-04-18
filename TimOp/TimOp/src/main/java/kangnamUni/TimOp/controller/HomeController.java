package kangnamUni.TimOp.controller;

import jakarta.servlet.http.HttpSession;
import kangnamUni.TimOp.Service.LectureService;
import kangnamUni.TimOp.Service.MemberService;
import kangnamUni.TimOp.Service.TimetableService;
import kangnamUni.TimOp.domain.*;
import kangnamUni.TimOp.dto.LectureDTO;
import kangnamUni.TimOp.dto.MemberDTO;
import kangnamUni.TimOp.dto.MemberDetails;
import kangnamUni.TimOp.dto.TimetableDTO;
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
    public ResponseEntity<List<LectureDTO>> getTimetableLectures(@PathVariable("timetableName") String timetableName, HttpSession session){
        Member member = (Member) session.getAttribute("loginMember");
        Timetable timetable = timetableService.findByNameAndMember(timetableName, member);

        List<LectureDTO> lectureDTOs = timetableService.convertTimetableLectureDTOs(timetable);

        return ResponseEntity.ok(lectureDTOs);
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
        Member member = (Member) session.getAttribute("loginMember");

        timetableService.addLectureToTimetable(member.getId(), timetableName, lectureId);
        Timetable timetable = timetableService.findByNameAndMember(timetableName, member);
        TimetableDTO timetableDTO = timetableService.convertTimetableDTO(timetable);

        model.addAttribute("member", member);
        // 리다이렉트 또는 뷰 이름 반환
        return ResponseEntity.ok(timetableDTO);
    }
    @DeleteMapping("/timetables/{timetableName}/lectures/{lectureId}")
    @ResponseBody
    public ResponseEntity<TimetableDTO> deleteTimetableLecture(@PathVariable("timetableName") String timetableName, @PathVariable("lectureId") Long lectureId, HttpSession session){
        Member member = (Member) session.getAttribute("loginMember");
        Timetable timetable = timetableService.deleteTimetableLecture(member.getId(), timetableName, lectureId);
        TimetableDTO timetableDTO = timetableService.convertTimetableDTO(timetable);
        return ResponseEntity.ok(timetableDTO);
    }
    //시간표 표시
    @GetMapping("/timetables/{timetableName}")
    @ResponseBody
    public ResponseEntity<TimetableDTO> getTimetable(@PathVariable("timetableName") String timetableName, HttpSession session) {
        Member member = (Member) session.getAttribute("loginMember");
        Timetable timetable = timetableService.findByNameAndMember(timetableName, member);
        TimetableDTO timetableDTO = timetableService.convertTimetableDTO(timetable);

        return ResponseEntity.ok(timetableDTO);
    }
    @GetMapping("/timetables")
    @ResponseBody
    public ResponseEntity<List<TimetableDTO>> getAllTimetables() {
        return ResponseEntity.ok(timetableService.getAllTimetablesInfo());
    }

    //시간표 추가
    @PostMapping("/timetables")
    public String addTimetable(@RequestParam ("name") String timetableName, Model model, HttpSession session) {
        Member member = (Member) session.getAttribute("loginMember");

        if(timetableService.existsByName(timetableName, member.getId())){
            //예외처리 알림 추가해
            model.addAttribute("member", member);
            List<Timetable> timetables = timetableService.findByMemberId(member.getId());

            return "redirect:/home";
        }
        Timetable newTimetable = timetableService.createTimetableForMember(member.getId(), timetableName);

        model.addAttribute("member", member);
        List<Timetable> timetables = timetableService.findByMemberId(member.getId());

        return "redirect:/home";
    }
    @DeleteMapping("/timetables/{name}")
    public ResponseEntity<?> deleteTimetable(@ModelAttribute TimetableDTO timetableDTD, Model model, HttpSession session) {
        log.info("삭제 시간표 이름" + timetableDTD.getName());
        Member member = (Member) session.getAttribute("loginMember");
        timetableService.deleteTimetableForMember(member.getId(), timetableDTD.getName());

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
