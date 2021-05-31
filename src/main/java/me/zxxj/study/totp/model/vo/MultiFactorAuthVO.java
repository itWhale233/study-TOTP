package me.zxxj.study.totp.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.zxxj.study.totp.model.enums.MFAType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MultiFactorAuthVO {

    private String qrImage;

    private String optAuthUrl;

    private String mfaKey;

    private MFAType mfaType;

    public MultiFactorAuthVO(MFAType mfaType) {
        this.mfaType = mfaType;
    }
}

