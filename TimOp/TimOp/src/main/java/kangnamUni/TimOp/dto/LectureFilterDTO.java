package kangnamUni.TimOp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@AllArgsConstructor
public class LectureFilterDTO {
    private String title;
    private String time;
    private String major;
    private List<Integer> grade;
    private List<Integer> credit;
    private List<String> liberalArts;

    private String availability; //custom, empty
    private List<String> days;
    private List<String> startTimes;
    private List<String> endTimes;

}
