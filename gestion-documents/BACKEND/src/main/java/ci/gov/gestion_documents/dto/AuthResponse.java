package ci.gov.gestion_documents.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private String message;

    public AuthResponse(String jwtToken, String bearer) {
        this.token = jwtToken;
        this.tokenType = "Bearer";
    }
}
