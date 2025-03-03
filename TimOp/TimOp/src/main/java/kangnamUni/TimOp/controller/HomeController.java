package kangnamUni.TimOp.controller;

import jakarta.servlet.http.HttpSession;
import kangnamUni.TimOp.Service.LectureService;
import kangnamUni.TimOp.Service.MemberService;
import kangnamUni.TimOp.Service.TimetableService;
import kangnamUni.TimOp.domain.*;
import kangnamUni.TimOp.repository.LectureRepository;
import kangnamUni.TimOp.repository.LectureRepositoryCustomImpl;
import kangnamUni.TimOp.repository.MemberRepository;
import kangnamUni.TimOp.repository.TimetableRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private LectureRepository lectureRepository;
    @Autowired
    private LectureService lectureService;
    @Autowired
    private TimetableRepository timetableRepository;
    @Autowired
    private TimetableService timetableService;

    @GetMapping("/home")
    public String home(Model model, HttpSession session){

        Member member = (Member) session.getAttribute("loginMember");
        //List<Timetable> timetables = member.getTimetables();
        //model.addAttribute("timetables", timetables);
        model.addAttribute("member", member);
        List<Timetable> timetables = timetableService.findByMemberId(member.getId());
        model.addAttribute("timetables", timetables);

        return "home";
    }

    @GetMapping("/")
    public String frontPage(){
        return "front";
    }
    @GetMapping("/signup")
    public String signup(Model model){
        model.addAttribute("member", new Member());
        return "signup";
    }
    @PostMapping("/signup")
    public String signUp(@ModelAttribute Member member) {

        if (memberService.checkStudentIdDuplicate(member.getStudentId())){
            return "signup";
        };
        memberRepository.save(member);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("studentId") String studentId, @RequestParam("password") String password, Model model, HttpSession session) {
        Member member = memberService.login(studentId, password);
        if (member != null) {
            session.setAttribute("loginMember", member);
            model.addAttribute("member", member);
            List<Timetable> timetables = timetableService.findByMemberId(member.getId());
            model.addAttribute("timetables", timetables);
            return "home";
        } else {
            return "login";
        }
    }
    //DB 시간표에 강의 저장
    @PostMapping("/timetables/lectures")
    public String addLecture(@RequestParam("id") Long lectureId, @RequestParam("timetableName") String timetableName, Model model, HttpSession session) {
        // lectureId를 사용하여 필요한 작업 수행
        // 예: lectureId를 사용하여 데이터베이스에서 강의 정보 조회 및 처리
        Member member = (Member) session.getAttribute("loginMember");
        log.info(timetableName);
        timetableService.addLectureToTimetable(member.getId(), timetableName, lectureId);

        model.addAttribute("member", member);
        List<Timetable> timetables = timetableService.findByMemberId(member.getId());
        model.addAttribute("timetables", timetables);
        log.info("강의 저장");
        // 리다이렉트 또는 뷰 이름 반환
        return "redirect:/home"; // 강의 목록 페이지로 리다이렉트
    }
    //시간표 표시
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
            model.addAttribute("timetables", timetables);

            return "redirect:/home";
        }
        Timetable newTimetable = timetableService.createTimetableForMember(member.getId(), timetableName);

        model.addAttribute("member", member);
        List<Timetable> timetables = timetableService.findByMemberId(member.getId());
        model.addAttribute("timetables", timetables);

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
