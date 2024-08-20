package com.licheng.pmLoginService.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.licheng.pmLoginService.entity.User;
import com.licheng.pmLoginService.entity.UserToken;
import com.licheng.pmLoginService.mapper.UserMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

@Slf4j
@Service
@Transactional
public class UserService extends ServiceImpl<UserMapper, User> implements IService<User> {
    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCNtt62S50Xxmc0OXmT2Zibouvsimvd6z//01qIjXT0oV2QcCloBxkhmfaHYgt94+vY997HHhMqPMLvUd7vBCjLtkRiXm5CG1jyU6XYxDPG9iCYbpLGBXow228T48wWg8fI3pK6D115hQi4NxA5F9kebsTnQSL3SRlA12DsgCnlswIDAQAB";
    private String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAI223rZLnRfGZzQ5eZPZmJui6+yKa93rP//TWoiNdPShXZBwKWgHGSGZ9odiC33j69j33sceEyo8wu9R3u8EKMu2RGJebkIbWPJTpdjEM8b2IJhuksYFejDbbxPjzBaDx8jekroPXXmFCLg3EDkX2R5uxOdBIvdJGUDXYOyAKeWzAgMBAAECgYA8CSDIsbfXfqHyKfcrf597L8Yq62lPFn6CbpOIDgViLYppylqAS6GF7qfWKt8bchdUT439ZOxRlHQw1spPD8AICYQdCYqg7erPB1PLaquRHgWsWCQtBkKAC8aq1IKYsy0aWn0eXtRATYNa0dBGUmb4gEtLAPMtno/h4joZZLmfQQJBAOE1Uoy/zb5XF4hsbYd6BlqTEK0e29Oe5JHvCOxWRjihqQ1dzNNuQ/6ldrIWdPasYrUx65ovv9LBZPjshu+10fsCQQChFx2Pl2rORnJvI9q1rAi8GBy03wKgELgcJZVbSZSjdyP9Gf6knntTrX82dis6UQkcWMoLELxVSGasqeqcCyWpAkA8d1CesbDP9ho/ar1NrpmeH/ZORQbIntTHyeIfclqQQfddu12PNv/SkyJ3Vkg5Iret5qVXVJKwR3SQ0RC5s5uZAkBt0Zpfd7RiZ3g/+EtzqpJR2IZJb/cUNBGvbpfBogno3qH0PDJy+2/L/e1JrYmOugvh/U3P+VrLMQ5+C2BbfOXxAkBCDUiNxsi+TVbdz9HFXkiv4ShT8nhUo06D6oPvZQJOctIFPqFKGk3OS0G8zBzTyOKDu4fKWMIdDHxYqNfoi25q";
    private final Map<String, UserToken> loggedInUserToken = new HashMap<>();
    @Getter
    private UserToken currentUser = new UserToken();

    public UserToken getLoginUser(String token){
        return this.loggedInUserToken.get(token);
    }

    public Boolean isUserAuthorized(String token)  {
        if (this.loggedInUserToken.containsKey(token)) {
            this.currentUser = this.loggedInUserToken.get(token);
        }
        return this.loggedInUserToken.containsKey(token);
    }

    public String md5(String input){
        String hash = "";
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            log.error("",e);
        }
        md.update(input.getBytes());
        byte[] output = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < output.length; i++) {
            byte b = output[i];
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        hash = sb.toString();
        return hash;
    }

    public Boolean login(String password) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getPasswordSalted, password);
        User user = getOne(qw);
        if (user == null) {
            return false;
        }
        UserToken userToken = new UserToken();
        userToken.setUserName(user.getUsername());
        userToken.setExpTime(new Date());// todo: 这里放过期时间，之后做
        this.loggedInUserToken.put(password, userToken);
        return true;
    }

    public Boolean register(String encryptedText) {
        Cipher cipher = null;
        String jsonString = "";
        try {
            cipher = Cipher.getInstance("RSA");
            byte[] privateKeyBytes = new BASE64Decoder().decodeBuffer(privateKey);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
            cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
            jsonString = new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText.getBytes())));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");

        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getUsername, username);
        List<User> userList = list(qw);
        if (userList.size() == 0) {
            User user = new User();
            user.setUUID(UUID.randomUUID().toString());
            user.setUsername(username);
            user.setPasswordSalted(password);
            save(user);
            return true;
        }
        return false;
    }
}

