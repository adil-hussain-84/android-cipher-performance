package com.masabi.androidcipherperformance;

import android.support.annotation.NonNull;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AESBytesEncryptor {

    /** The {@link Cipher} instance to use for encryption. */
    @NonNull
    private final Cipher cipherEncrypt;

    /** The {@link SecretKey} instance to use for encryption. */
    @NonNull
    private final SecretKey secretKey;

    /**
     * Constructor.
     *
     * @param secretKey the key to use for encryption.
     * @throws CryptoException
     */
    public AESBytesEncryptor(@NonNull SecretKey secretKey) throws CryptoException {
        this.secretKey = secretKey;

        try {
            cipherEncrypt = Cipher.getInstance("AES/CBC/PKCS7PADDING");
        } catch (Exception ex) {
            String message = "Failed setting up Cipher instances for encryption and decryption";
            throw new CryptoException(message, ex);
        }
    }

    @NonNull
    public byte[] encrypt(@NonNull byte[] inputBytes,
                          @NonNull byte[] initVectorBytes) throws CryptoException {
        try {
            AlgorithmParameterSpec params = new IvParameterSpec(initVectorBytes);

            synchronized (cipherEncrypt) {
                cipherEncrypt.init(Cipher.ENCRYPT_MODE, secretKey, params);
                return cipherEncrypt.doFinal(inputBytes);
            }
        } catch (Exception ex) {
            String message = "Failed encryption";
            throw new CryptoException(message, ex);
        }
    }
}
