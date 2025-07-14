package acc.firewatch.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequestDto {
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;
}
