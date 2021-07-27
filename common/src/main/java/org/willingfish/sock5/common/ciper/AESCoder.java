package org.willingfish.sock5.common.ciper;

import io.netty.util.internal.StringUtil;
import lombok.Setter;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AESCoder {
    @Setter
    String password;
    SecretKeySpec skeySpec;

    void init(){
        if (StringUtil.isNullOrEmpty(password)){
            password = "1234567890123456";
        }else if (password.getBytes(StandardCharsets.UTF_8).length!=16){
            password = "1234567890123456";
        }
        byte[] raw = password.getBytes(StandardCharsets.UTF_8);
        skeySpec = new SecretKeySpec(raw, "AES");
    }

    public byte[] encrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(data);
        return encrypted;
    }

    public byte[] decrypt(byte[] encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] data = cipher.doFinal(encrypted);
        return data;
    }
}
