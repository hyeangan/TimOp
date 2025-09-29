package kangnamUni.TimOp.dto;

import lombok.Data;

@Data
public class GoogleTokenResponseDTO {
    String accessToken;
    String idToken;
    int expiresIn;
    public GoogleTokenResponseDTO(String accessToken, String idToken, int expiresIn) {
        this.accessToken = accessToken;
        this.idToken = idToken;
        this.expiresIn = expiresIn;
    }
}
