package kangnamUni.TimOp.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class LectureDTO {
    private Long id;
    private String num; //학수번호
    private String title;
    private String professor;
    private int credit; //학점
    private String syllabus;
    private int grade;
    private List<LectureTimeDTO> lectureTimes;
    public LectureDTO(String title){
        this.title = title;
    }
    public LectureDTO(Long id, String num, String title, String professor, int credit, String syllabus, int grade, List<LectureTimeDTO> lectureTimes) {
        this.id = id;
        this.num = num;
        this.title = title;
        this.professor = professor;
        this.credit = credit;
        this.syllabus = syllabus;
        this.grade = grade;
        this.lectureTimes = lectureTimes;
    }
}
