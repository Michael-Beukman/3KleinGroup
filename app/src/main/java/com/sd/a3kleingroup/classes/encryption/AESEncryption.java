package com.sd.a3kleingroup.classes.encryption;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.util.Base64;

/**
 * This class uses symmetric key encryption for encrypting an inputStream.
 *
 */
public class AESEncryption {
    private SecretKey secretKey;

    /**
     * Constructor
     * @param keyLength The length of the key
     */
    public AESEncryption(int keyLength){
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(keyLength); // The AES key size in number of bits
            secretKey = generator.generateKey();
        }catch (Exception e){}
    }

    /**
     * Constructor
     * Uses a keylength of 128
     */
    public AESEncryption (){
        this(128);
    }

    /**
     * Constructs with a base64 key.
     * @param key
     */
    public AESEncryption(String key){
        byte[] decodedKey = Base64.getDecoder().decode(key);
        // rebuild key using SecretKeySpec
        secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    /**
     * Encrypts the IS using AES
     * @param stream
     * @return
     */
    public InputStream encrypt(InputStream stream){
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            CipherInputStream cipherInputStream = new CipherInputStream(stream, cipher);
            return cipherInputStream;
        }catch (Exception e){
            return stream;
        }
    }

    /**
     * Decrypts the IS using AES
     * @param stream
     * @return
     */
    public InputStream decrypt(InputStream stream){
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            CipherInputStream cipherInputStream = new CipherInputStream(stream, cipher);
            return cipherInputStream;
        }catch (Exception e){
            return stream;
        }
    }

    /**
     * Returns the key as a base64 encoded string.
     * @return
     */
    public String getKey(){
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
}
