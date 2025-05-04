package kangnamUni.TimOp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@AllArgsConstructor
public class LectureFilterDTO {
    @Schema(description = "강의명 키워드 (포함 검색)", example = "컴퓨")
    private String title;
    private String time;
    private String major;
    private List<Integer> grade;
    @Schema(description = "학점", example = "0학점, 1학점, 2학점, 3학점")
    private List<Integer> credit;
    @Schema(description = "교양 영역", example = "1영역, 2영역, 3영역, 4영역, 5영역, 자선, 일반")
    private List<String> liberalArts;
    @Schema(description = "현재 시간표 이름(빈 시간대 검색을 위해)", example = "시간표1")
    private String timetableName;
    @Schema(description = "시간대 검색 빈시간대/시간대선택", example = "custom, empty")
    private String availability;

    @Schema(description = "시간대 검색 요일", example = "[월, 월, 화, 금]")
    private List<String> days;
    @Schema(description = "시간대 검색 시간시간", example = "[1:30, 09:39, 4:30, 09:00]")
    private List<String> startTimes;
    @Schema(description = "시간대 검색 끝나는시간", example = "[3:30, 12:39, 7:30, 12:00]")
    private List<String> endTimes;

}
