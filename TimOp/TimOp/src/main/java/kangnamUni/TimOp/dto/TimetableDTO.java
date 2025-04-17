package kangnamUni.TimOp.dto;

import lombok.Data;

import java.util.List;

@Data
public class TimetableDTO {
    private Long id;
    private String name;
    private List<LectureDTO> lectures;

    public TimetableDTO(Long id, String name, List<LectureDTO> collect) {
        this.id = id;
        this.name = name;
        this.lectures = collect;
    }
}
