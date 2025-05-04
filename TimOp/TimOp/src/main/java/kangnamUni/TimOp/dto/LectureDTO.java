package kangnamUni.TimOp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kangnamUni.TimOp.domain.Lecture;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class LectureDTO {
    private Long id;
    private String num; //학수번호
    @Schema(description = "강의 제목", example = "자료구조")
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
    public static LectureDTO from(Lecture lec) {
        return new LectureDTO(
                lec.getId(),
                lec.getNum(),
                lec.getTitle(),
                lec.getProfessor(),
                lec.getCredit(),
                lec.getSyllabus(),
                lec.getGrade(),
                lec.getLectureTimes().stream()
                        .map(t -> new LectureTimeDTO(
                                t.getDayOfWeek().toString(),
                                t.getStartTime(),
                                t.getEndTime()))
                        .collect(Collectors.toList())
        );
    }
}
