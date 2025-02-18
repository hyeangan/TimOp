package kangnamUni.TimOp.controller;

import kangnamUni.TimOp.Service.LectureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class TestController {
    private final LectureService lectureService;
    @Autowired
    public TestController(LectureService lectureService){
        this.lectureService = lectureService;
    }
    @GetMapping("/test")
    public String test(){
        return "redirect:/syllabus-da01601-01.html";
    }
}
