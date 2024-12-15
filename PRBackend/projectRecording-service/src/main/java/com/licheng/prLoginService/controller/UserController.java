package com.licheng.prLoginService.controller;

import com.licheng.prLoginService.service.UserService;
import com.licheng.prMainService.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;


@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/login")
    public Result login(@RequestParam String token){
        Result result = new Result();
        Boolean success = userService.login(token);
        result.setSuccess(success);
        if (success) {
            result.setResult(token);
        }
        return result;
    }

    @PostMapping("/register")
    public Result register(@RequestBody String encryptedText) {
        Result result = new Result();
        result .setSuccess(userService.register(encryptedText));
        return result;
    }

    // 下面的是用来测试非对称加密的代码。
//    public static void main(String[] args) throws Exception{
//        String cipheredText = "eT9cXMdE7UmoDU6Ee0zxjISUFV794yUmHIgW5zs18AQ3Q1Wbu7PlIlvcUSJ+ek2jScVdNoF8YTBPqdTbt+drtBTyFEpDwRoKG/ffOU43VE2FU5CkpqzWCyKCRITA9xsblWsZr8li1FZJYDjjHaiVj7Mzipc2szxJvSYD8K5D2zw=";
//        String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAI223rZLnRfGZzQ5eZPZmJui6+yKa93rP//TWoiNdPShXZBwKWgHGSGZ9odiC33j69j33sceEyo8wu9R3u8EKMu2RGJebkIbWPJTpdjEM8b2IJhuksYFejDbbxPjzBaDx8jekroPXXmFCLg3EDkX2R5uxOdBIvdJGUDXYOyAKeWzAgMBAAECgYA8CSDIsbfXfqHyKfcrf597L8Yq62lPFn6CbpOIDgViLYppylqAS6GF7qfWKt8bchdUT439ZOxRlHQw1spPD8AICYQdCYqg7erPB1PLaquRHgWsWCQtBkKAC8aq1IKYsy0aWn0eXtRATYNa0dBGUmb4gEtLAPMtno/h4joZZLmfQQJBAOE1Uoy/zb5XF4hsbYd6BlqTEK0e29Oe5JHvCOxWRjihqQ1dzNNuQ/6ldrIWdPasYrUx65ovv9LBZPjshu+10fsCQQChFx2Pl2rORnJvI9q1rAi8GBy03wKgELgcJZVbSZSjdyP9Gf6knntTrX82dis6UQkcWMoLELxVSGasqeqcCyWpAkA8d1CesbDP9ho/ar1NrpmeH/ZORQbIntTHyeIfclqQQfddu12PNv/SkyJ3Vkg5Iret5qVXVJKwR3SQ0RC5s5uZAkBt0Zpfd7RiZ3g/+EtzqpJR2IZJb/cUNBGvbpfBogno3qH0PDJy+2/L/e1JrYmOugvh/U3P+VrLMQ5+C2BbfOXxAkBCDUiNxsi+TVbdz9HFXkiv4ShT8nhUo06D6oPvZQJOctIFPqFKGk3OS0G8zBzTyOKDu4fKWMIdDHxYqNfoi25q";

//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA"); // 密钥长度为 2048 位
//        keyPairGenerator.initialize(1024); // 生成密钥对
//        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
//        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
//        System.out.println(Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded()));
//        System.out.println(Base64.getEncoder().encodeToString(rsaPrivateKey.getEncoded()));

//        Cipher cipher = Cipher.getInstance("RSA");
//        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
//        String text = "admin123456";
//        cipheredText = new String (Base64.getEncoder().encode(cipher.doFinal(text.getBytes())));
//
//        privateKey = Base64.getEncoder().encodeToString(rsaPrivateKey.getEncoded());
//
//        byte[] privateKeyBytes = new BASE64Decoder().decodeBuffer(privateKey);
//        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
//        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
//
//        byte[] input = Base64.getDecoder().decode(cipheredText);
//        Cipher cipher = Cipher.getInstance("RSA");
//        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
//        String out = new String(cipher.doFinal(input));
//        System.out.println(out);
//    }
}

