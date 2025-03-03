package kangnamUni.TimOp.domain;

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
}
