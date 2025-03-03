package kangnamUni.TimOp.domain;

import lombok.Data;

import java.util.List;

@Data
public class LectureDTO {
    private Long id;
    private String title;
    private String professor;
    private List<LectureTimeDTO> lectureTimes;

    public LectureDTO(String title, String professor, List<LectureTimeDTO> collect) {
        this.title = title;
        this.professor = professor;
        this.lectureTimes = collect;
    }
}
