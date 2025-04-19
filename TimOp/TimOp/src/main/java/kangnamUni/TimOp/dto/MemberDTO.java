package kangnamUni.TimOp.dto;

import lombok.Data;

//home 화면에 사용자 정보 표시용 DTO
@Data
public class MemberDTO {
    String name;

    public MemberDTO(String name) {
        this.name = name;
    }
}
