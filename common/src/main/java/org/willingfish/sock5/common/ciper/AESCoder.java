package org.willingfish.sock5.common.ciper;

import lombok.Setter;

public class AESCoder {
    @Setter
    String password;

    public byte[] encrypt(byte[] data){
        return data;
    }

    public byte[] decrypt(byte[] cipher){
        return cipher;
    }
}
