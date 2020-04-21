package com.sd.a3kleingroup;


import com.sd.a3kleingroup.classes.encryption.AESEncryption;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class AESTest {

    @org.junit.Test
    public void encrypt() {
        AESEncryption aes = new AESEncryption();
        byte[] initialArray = { 0, 1, 2, 3, 4, 5 };
        InputStream targetStream = new ByteArrayInputStream(initialArray);
        InputStream encrypted = aes.encrypt(targetStream);
        byte[] encryptedArr = getByteArrayFromIS(encrypted);
        assertNotEquals(initialArray, encryptedArr);
        assertArrayEquals(initialArray, new byte[]{ 0, 1, 2, 3, 4, 5 });
    }

    @org.junit.Test
    public void decrypt() {
        AESEncryption aes = new AESEncryption();

        byte[] initialArray = { 0, 1, 2, 3, 4, 5 };
        InputStream targetStream = new ByteArrayInputStream(initialArray);
        InputStream encrypted = aes.encrypt(targetStream);
        InputStream decrypted = aes.decrypt(encrypted);
        byte[] ans = getByteArrayFromIS(decrypted);
        assertArrayEquals(initialArray, ans);
    }


    @org.junit.Test
    public void testKeys(){
        AESEncryption aes = new AESEncryption();

        String key = aes.getKey();
        byte[] arr = new byte[]{1,3,4,5,6,7,8,9};
        InputStream targetStream = new ByteArrayInputStream(arr);
        InputStream encrypted = aes.encrypt(targetStream);

        // make a new one from the previous keys
        AESEncryption aes2 = new AESEncryption(key);

        InputStream decrypted = aes2.decrypt(encrypted);

        assertArrayEquals(arr, getByteArrayFromIS(decrypted));
    }

    byte[] getByteArrayFromIS(InputStream is){
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];
        try {
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new byte[0];
        }
        return buffer.toByteArray();
    }
}