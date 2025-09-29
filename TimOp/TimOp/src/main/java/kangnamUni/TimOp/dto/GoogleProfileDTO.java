package kangnamUni.TimOp.dto;

import lombok.Data;

@Data
public class GoogleProfileDTO {
    String sub; // sub: 구글 사용자 고유 ID
    String email;
    boolean emailVerified;
    String name;
    String picture;

    public GoogleProfileDTO(String sub, String email, boolean emailVerified, String name, String picture) {
        this.sub = sub;
        this.email = email;
        this.emailVerified = emailVerified;
        this.name = name;
        this.picture = picture;
    }
}
