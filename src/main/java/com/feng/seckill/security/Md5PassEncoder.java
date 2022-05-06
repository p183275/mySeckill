package com.feng.seckill.security;

import com.feng.seckill.util.MD5Utils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author : pcf
 * @date : 2022/4/11 17:03
 */
@Component
public class Md5PassEncoder implements PasswordEncoder {


    @Override
    public String encode(CharSequence charSequence) {
        return MD5Utils.encrypt(charSequence.toString());
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return s.equals(MD5Utils.encrypt(charSequence.toString()));
    }
}
