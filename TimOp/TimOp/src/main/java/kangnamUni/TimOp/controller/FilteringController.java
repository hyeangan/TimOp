package kangnamUni.TimOp.controller;

import jakarta.servlet.http.HttpSession;
import kangnamUni.TimOp.Service.LectureService;
import kangnamUni.TimOp.Service.TimetableService;
import kangnamUni.TimOp.domain.*;
import kangnamUni.TimOp.dto.LectureDTO;
import kangnamUni.TimOp.dto.LectureFilterDTO;
import kangnamUni.TimOp.dto.LectureTimeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

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
    @ResponseBody
    public ResponseEntity<List<LectureDTO>> searchByTitle(@RequestParam("title") String title, Model model, HttpSession session) {
        List<Lecture> lectures = lectureService.findLecturesByTitleContaining(title);
        Member member = (Member) session.getAttribute("loginMember");
        model.addAttribute("member", member);
        List<Timetable> timetables = timetableService.findByMemberId(member.getId());
        List<LectureDTO> lectureDTOs = lectures.stream()
                .map(lecture -> new LectureDTO(
                        lecture.getId(),
                        lecture.getTitle(),
                        lecture.getProfessor(),
                        lecture.getLectureTimes().stream()
                                .map(lectureTime -> new LectureTimeDTO(lectureTime.getDayOfWeek().toString(), lectureTime.getStartTime(), lectureTime.getEndTime()))
                                .collect(Collectors.toList()) // ✅ `List<LectureTimeDTO>` 변환 후 저장
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(lectureDTOs);
    }

    //처음 @RequestParam 으로 모든 경우의 수에 대해 만들다 -> 변경
    @GetMapping("/search/condition")
    public ResponseEntity<List<LectureDTO>> searchByCondition(@ModelAttribute LectureFilterDTO filterDTO, Model model, HttpSession session) {
        List<Lecture> lectures = lectureService.findLectures(filterDTO);
        List<LectureDTO> lectureDTOs = lectures.stream()
                .map(lecture -> new LectureDTO(
                        lecture.getId(),
                        lecture.getTitle(),
                        lecture.getProfessor(),
                        lecture.getLectureTimes().stream()
                                .map(lectureTime -> new LectureTimeDTO(
                                        lectureTime.getDayOfWeek().toString(),
                                        lectureTime.getStartTime(),
                                        lectureTime.getEndTime()))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        Member member = (Member) session.getAttribute("loginMember");
        model.addAttribute("member", member);
        List<Timetable> timetables = timetableService.findByMemberId(member.getId());

        return ResponseEntity.ok(lectureDTOs);
    }
}
