package com.sd.a3kleingroup.classes.encryption;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import java.io.InputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Class RSA.
 * Basic Wrapper to encrypt and decrypt an InputStream using RSA
 */
public class RSA {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    /**
     * Generates keypairs on its own
     * @param keylength
     */
    public RSA(int keylength){
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(keylength);
            KeyPair pair = gen.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();
        }catch (Exception e){}
    }

    /**
     * Default constructor. Generates with keylength of 1024
     */
    public RSA(){
        this(1024);
    }

    /**
     * Construct from private and public keys (base64 encoded strings)
     * @param privateKey
     * @param publicKey
     */
    public RSA (String privateKey, String publicKey){
        PKCS8EncodedKeySpec privateKeyDecoder = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
        X509EncodedKeySpec publicKeyDecoder = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.privateKey = keyFactory.generatePrivate(privateKeyDecoder);
            this.publicKey = keyFactory.generatePublic(publicKeyDecoder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Encrypts this inputstream
     * @param stream
     * @return
     */
    public InputStream encrypt(InputStream stream){
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            CipherInputStream cipherInputStream = new CipherInputStream(stream, cipher);
            return cipherInputStream;
        }catch (Exception e){
            return stream;
        }
    }

    /**
     * Descrypts this inputstream
     * @param encrypted
     * @return
     */
    public InputStream decrypt(InputStream encrypted){
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            CipherInputStream decrypted = new CipherInputStream(encrypted, cipher);
            return decrypted;
        }catch (Exception e){
            return encrypted;
        }
    }

    /**
     * Gets the base64 encoded privateKey
     * @return
     */
    public String getPrivateKey(){
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * Gets the base64 encoded publicKey
     * @return
     */
    public String getPublicKey(){
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
}
