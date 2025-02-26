package kangnamUni.TimOp.controller;

import jakarta.servlet.http.HttpSession;
import kangnamUni.TimOp.Service.LectureService;
import kangnamUni.TimOp.Service.TimetableService;
import kangnamUni.TimOp.domain.Lecture;
import kangnamUni.TimOp.domain.LectureFilterDTO;
import kangnamUni.TimOp.domain.Member;
import kangnamUni.TimOp.domain.Timetable;
import kangnamUni.TimOp.repository.TimetableRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Controller
public class FilteringController {
    private final LectureService lectureService;
    private final TimetableService timetableService;

    @Autowired
    public FilteringController(LectureService lectureService, TimetableService timetableService) {
        this.lectureService = lectureService;
        this.timetableService = timetableService;
    }

    @GetMapping("/search")
    public String searchByTitle(@RequestParam("title") String title, Model model, HttpSession session) {
        List<Lecture> lectures = lectureService.findLecturesByTitleContaining(title);
        Member member = (Member) session.getAttribute("loginMember");
        //model.addAttribute("timetables", member.getTimetables());???흠;;
        //List<Timetable> timetables = member.getTimetables();
        //model.addAttribute("timetables", timetables);
        model.addAttribute("member", member);
        model.addAttribute("lectures", lectures);
        List<Timetable> timetables = timetableService.findByMemberId(member.getId());
        model.addAttribute("timetables", timetables);
        return "home";
    }

    //처음 @RequestParam 으로 모든 경우의 수에 대해 만들다 -> 변경
    @GetMapping("/search/condition")
    public String searchByCondition(@ModelAttribute LectureFilterDTO filterDTO, Model model, HttpSession session) {
        List<Lecture> lectures = lectureService.findLectures(filterDTO);
        Member member = (Member) session.getAttribute("loginMember");
        //List<Timetable> timetables = member.getTimetables();
        //model.addAttribute("timetables", timetables);
        model.addAttribute("member", member);
        model.addAttribute("lectures", lectures);
        List<Timetable> timetables = timetableService.findByMemberId(member.getId());
        model.addAttribute("timetables", timetables);
        return "home";

    }
}
