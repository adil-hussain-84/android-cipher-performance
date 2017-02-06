package com.masabi.androidcipherperformance;

import android.support.annotation.NonNull;
import android.util.Log;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AESBytesDecryptor {

    private static final String TAG = AESBytesDecryptor.class.getSimpleName();

    @NonNull
    private final Cipher cipherDecrypt;

    /** The key to use for decryption. */
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
            Log.e(TAG, message);
            throw new CryptoException(message, ex);
        }
    }

    @NonNull
    public byte[] decrypt(@NonNull byte[] inputBytes,
                          @NonNull byte[] initVectorBytes) throws CryptoException {
        try {
            AlgorithmParameterSpec params = getAlgorithmParameterSpec(initVectorBytes);

            synchronized (cipherDecrypt) {
                cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKey, params);
                return cipherDecrypt.doFinal(inputBytes);
            }
        } catch (Exception ex) {
            String message = "Failed decryption";
            Log.e(TAG, message);
            throw new CryptoException(message, ex);
        }
    }

    /**
     * Wraps the given initialization vector in a {@link AlgorithmParameterSpec} object.
     *
     * @param initVectorBytes is a byte array that has a length value which is suitable for an AES encryption initialization vector.
     * @return a new {@link AlgorithmParameterSpec} object that wraps {@code initVectorBytes}.
     */
    @NonNull
    private AlgorithmParameterSpec getAlgorithmParameterSpec(@NonNull byte[] initVectorBytes) {
        return new IvParameterSpec(initVectorBytes);
    }
}
