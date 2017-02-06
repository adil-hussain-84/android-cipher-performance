package com.masabi.androidcipherperformance;

import android.support.annotation.NonNull;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AESBytesDecryptor {

    /** The {@link Cipher} instance to use for decryption. */
    @NonNull
    private final Cipher cipherDecrypt;

    /** The {@link SecretKey} instance to use for decryption. */
    @NonNull
    private final SecretKey secretKey;

    /**
     * Constructor.
     *
     * @param secretKey the key to use for decryption.
     * @throws CryptoException
     */
    public AESBytesDecryptor(@NonNull SecretKey secretKey) throws CryptoException {
        this.secretKey = secretKey;

        try {
            cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS7PADDING");
        } catch (Exception ex) {
            String message = "Failed setting up Cipher instances for encryption and decryption";
            throw new CryptoException(message, ex);
        }
    }

    @NonNull
    public byte[] decrypt(@NonNull byte[] inputBytes,
                          @NonNull byte[] initVectorBytes) throws CryptoException {
        try {
            AlgorithmParameterSpec params = new IvParameterSpec(initVectorBytes);

            synchronized (cipherDecrypt) {
                cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKey, params);
                return cipherDecrypt.doFinal(inputBytes);
            }
        } catch (Exception ex) {
            String message = "Failed decryption";
            throw new CryptoException(message, ex);
        }
    }
}
