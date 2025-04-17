package kangnamUni.TimOp.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class LectureDTO {
    private Long id;
    private String title;
    private String professor;
    private List<LectureTimeDTO> lectureTimes;

    public LectureDTO(Long id, String title, String professor, List<LectureTimeDTO> collect) {
        this.id = id;
        this.title = title;
        this.professor = professor;
        this.lectureTimes = collect;
    }
}
