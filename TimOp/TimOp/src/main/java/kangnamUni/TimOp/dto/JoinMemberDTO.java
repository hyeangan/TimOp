package kangnamUni.TimOp.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class JoinMemberDTO {
    private String studentId;
    private String password;
    private String name;
    private String major;
}
