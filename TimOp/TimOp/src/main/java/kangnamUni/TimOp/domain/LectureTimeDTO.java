package kangnamUni.TimOp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
public class LectureTimeDTO {
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public LectureTimeDTO(String dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
