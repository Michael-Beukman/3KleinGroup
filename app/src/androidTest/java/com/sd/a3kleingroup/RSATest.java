package com.sd.a3kleingroup;

import com.sd.a3kleingroup.classes.encryption.RSA;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import static org.junit.Assert.*;

//@RunWith(JUnit4.class)
public class RSATest {
    /*
    @org.junit.Test
    public void encrypt() {
        RSA rsa = new RSA();
        byte[] initialArray = { 0, 1, 2, 3, 4, 5 };
        InputStream targetStream = new ByteArrayInputStream(initialArray);
        InputStream encrypted = rsa.encrypt(targetStream);
        byte[] encryptedArr = getByteArrayFromIS(encrypted);
        assertNotEquals(initialArray, encryptedArr);
        assertArrayEquals(initialArray, new byte[]{ 0, 1, 2, 3, 4, 5 });
    }

    @org.junit.Test
    public void decrypt() {
        RSA rsa = new RSA();
        byte[] initialArray = { 0, 1, 2, 3, 4, 5 };
        InputStream targetStream = new ByteArrayInputStream(initialArray);
        InputStream encrypted = rsa.encrypt(targetStream);
        InputStream decrypted = rsa.decrypt(encrypted);
        byte[] ans = getByteArrayFromIS(decrypted);
        assertArrayEquals(initialArray, ans);
    }


    @org.junit.Test
    public void testKeys(){
        RSA rsa = new RSA(); // auto generate keys
        String privateKey = rsa.getPrivateKey();
        String publicKey = rsa.getPublicKey();
        byte[] arr = new byte[]{1,3,4,5,6,7,8,9};
        InputStream targetStream = new ByteArrayInputStream(arr);
        InputStream encrypted = rsa.encrypt(targetStream);
        System.out.println("PublicKey " + publicKey);
        System.out.println("PrivateKey " + privateKey);

        // make a new one from the previous keys
        RSA newRSA = new RSA(privateKey, publicKey);

        InputStream decrypted = newRSA.decrypt(encrypted);

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

    */
}