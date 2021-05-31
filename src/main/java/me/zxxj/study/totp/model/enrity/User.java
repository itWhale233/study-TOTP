package me.zxxj.study.totp.model.enrity;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.zxxj.study.totp.model.enums.MFAType;

@Data
@AllArgsConstructor
public class User {

    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private MFAType mfaType;
    private String mfaKey;
}
