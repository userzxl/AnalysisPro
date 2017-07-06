/**
 * 
 */
package com.tbox.discard;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.tbox.service.Functions;

/**
 * @author ZhaoXiaolong
 * 2017-7-5上午10:43:45
 */
public class Test2  {
    SecretKey deskey = null;// 加解密必须使用相同的key，所以需要用实例变量控制
 
    public byte[] decryption(byte[] s) {
    	byte[] hehekey = {0x1E,0x3F,0x32,0x5D,(byte) 0x9F,0x6B,(byte) 0xA6,0x7A,(byte) 0xD8,(byte) 0xDF,0x3E,(byte) 0xEA,0x6D,0x09,0x60,(byte) 0xED};
        System.out.println("解密开始...");
        Cipher c = null;
        byte[] enc = null;
        try {
            // c = Cipher.getInstance("DESede");
            c = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        try {
            c.init(Cipher.DECRYPT_MODE, deskey);
            enc = c.doFinal(s);
            System.out.println("\t解密之后的明文是:" + new String(enc));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enc;
    }
 
    public byte[] encryption(String s) {
        System.out.println("加密开始...");
        System.out.println("\t原文：" + s);
        KeyGenerator keygen;
        Cipher c = null;
        try {
            // keygen = KeyGenerator.getInstance("DESede");
            keygen = KeyGenerator.getInstance("AES");
            byte[] rootKey = {0x19,(byte) 0xC1,0x4D,(byte) 0xAA,(byte) 0xB0,0x3C,0x77,0x43,0x68,0x5E,(byte) 0x8B,(byte) 0xF3,0x49,(byte) 0xE4,(byte) 0x9D,0x2A};
            keygen.init(128, new SecureRandom(rootKey));

            deskey = keygen.generateKey();
            // c = Cipher.getInstance("DESede");
            c = Cipher.getInstance("AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] dec = null;
        try {
            c.init(Cipher.ENCRYPT_MODE, deskey);
            byte[] hehekey = {0x01,0x02,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
            dec = c.doFinal(hehekey);
            System.out.print("\t加密后密文是:");
            for (byte b : dec) {
                System.out.print(b + ",");
            }
            System.out.println(new Functions().getHex(dec));
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dec;
    }
}
