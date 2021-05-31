package me.zxxj.study.totp.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.zxxj.study.totp.exception.BadRequestException;
import me.zxxj.study.totp.model.enrity.User;
import me.zxxj.study.totp.model.enums.MFAType;
import me.zxxj.study.totp.model.params.LoginParam;
import me.zxxj.study.totp.model.params.MultiFactorAuthParam;
import me.zxxj.study.totp.model.vo.MultiFactorAuthVO;
import me.zxxj.study.totp.utils.TwoFactorAuthUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/mfa")
public class TotpController {

    private HashMap<String, User> userHashMap = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        userHashMap.put("zhangsan", new User(1, "zhangsan", "zhangsan123", "张三", MFAType.NONE, null));
    }

    /**
     * 获取 MFA QR
     *
     * @param user
     * @return
     */
    @PutMapping("/generate")
    public MultiFactorAuthVO generateMFAQrImage(@RequestBody User user) {
        String mfaKey = TwoFactorAuthUtils.generateTFAKey();
        String optAuthUrl =
                TwoFactorAuthUtils.generateOtpAuthUrl(user.getNickname(), mfaKey);
        String qrImageBase64 = "data:image/png;base64,"
                + Base64.encode(QrCodeUtil.generatePng(optAuthUrl, 128, 128));
        return new MultiFactorAuthVO(qrImageBase64, optAuthUrl, mfaKey, MFAType.TFA_TOTP);
    }


    @PostMapping("/login")
    public String authCheck(@RequestBody LoginParam loginParam) throws JsonProcessingException {
        User user = userHashMap.get(loginParam.getUsername());
        if (user == null) {
            return "bad auth";
        }
        if (user.getPassword().equals(loginParam.getPassword())) {
            if (MFAType.useMFA(user.getMfaType())) {
                if (StrUtil.isBlank(loginParam.getAuthcode())) {
                    throw new BadRequestException("请输入两步验证码");
                }
                TwoFactorAuthUtils.validateTFACode(user.getMfaKey(), loginParam.getAuthcode());
            }

        }else{
            return "bad auth";
        }

        return objectMapper.writeValueAsString(user);
    }

    @PostMapping("/update")
    public MultiFactorAuthVO updateMFAuth(
            @RequestBody @Valid MultiFactorAuthParam multiFactorAuthParam) {
        User user = userHashMap.get("zhangsan");
        if (StrUtil.isNotBlank(user.getMfaKey())
                && MFAType.useMFA(multiFactorAuthParam.getMfaType())) {
            return new MultiFactorAuthVO(MFAType.TFA_TOTP);
        } else if (StrUtil.isBlank(user.getMfaKey())
                && !MFAType.useMFA(multiFactorAuthParam.getMfaType())) {
            return new MultiFactorAuthVO(MFAType.NONE);
        } else {
            final String tfaKey = StrUtil.isNotBlank(user.getMfaKey()) ? user.getMfaKey() :
                    multiFactorAuthParam.getMfaKey();
            TwoFactorAuthUtils.validateTFACode(tfaKey, multiFactorAuthParam.getAuthcode());
        }
        // update MFA key
        User updateUser = updateMFA(multiFactorAuthParam.getMfaType(), multiFactorAuthParam.getMfaKey(),
                user.getId());

        return new MultiFactorAuthVO(updateUser.getMfaType());
    }

    private User updateMFA(MFAType mfaType, String mfaKey, Integer id) {

        AtomicReference<User> user = new AtomicReference();
        userHashMap.forEach((k, v) -> {
            if (v.getId().equals(id)) {
                v.setMfaKey(mfaKey);
                v.setMfaType(mfaType);
                user.set(v);
            }
        });

        return user.get();
    }

}
