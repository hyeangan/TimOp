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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        List<Timetable> timetables = timetableRepository.findByMember((Member) session.getAttribute("loginMember"));
        model.addAttribute("timeTables", timetables);

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
            return "home";
        } else {
            return "login";
        }
    }
    //DB 시간표에 강의 저장
    @PostMapping("/lectures")
    public String addLecture(@RequestBody LectureDTO lectureDTO, HttpSession session) {
        // lectureId를 사용하여 필요한 작업 수행
        // 예: lectureId를 사용하여 데이터베이스에서 강의 정보 조회 및 처리
        Member member = (Member) session.getAttribute("loginMember");

        Lecture lecture = lectureService.getLectureById(lectureDTO.getId());

        // 필요한 경우, 모델에 데이터를 추가하여 뷰로 전달
        //model.addAttribute("message", "Lecture added successfully!");
        //db 시간표에 저장


        // 리다이렉트 또는 뷰 이름 반환
        return ""; // 강의 목록 페이지로 리다이렉트
    }
    @PostMapping("/timetables")
    public String addTimetable(@RequestBody TimetableDTO timetableDTD, Model model, HttpSession session) {
        Member member = (Member) session.getAttribute("loginMember");
        timetableService.createTimetableForMember(member.getId(), timetableDTD.getName());

        model.addAttribute("member", member);
        List<Timetable> timetables = timetableRepository.findByMember((Member) session.getAttribute("loginMember"));
        model.addAttribute("timeTables", timetables);

        return "home";
    }
    @DeleteMapping("/timetables")
    public String deleteTimetable(@RequestBody TimetableDTO timetableDTD, Model model, HttpSession session) {
        Member member = (Member) session.getAttribute("loginMember");
        timetableService.deleteTimetableForMember(member.getId(), timetableDTD.getName());

        model.addAttribute("member", member);
        List<Timetable> timetables = timetableRepository.findByMember((Member) session.getAttribute("loginMember"));
        model.addAttribute("timeTables", timetables);

        return "home";
    }
    //시간표에 강의 추가
    @PostMapping("/timetables/{timetableId}/lectures")
    public ResponseEntity<?> addTimetable(@PathVariable("timetableId") Long timetableId, @RequestBody Lecture lecture, HttpSession session) {
        try {

            Optional<Timetable> optionalTimetable = timetableRepository.findById(timetableId);
            if(optionalTimetable.isPresent()){
                Timetable timetable = optionalTimetable.get();
                Member member = (Member) session.getAttribute("loginMember");
                timetableService.addLectureToTimetable(member.getId(), timetable.getName(), lecture.getId());
                return ResponseEntity.ok(timetable);
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Timetable not found"));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
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
