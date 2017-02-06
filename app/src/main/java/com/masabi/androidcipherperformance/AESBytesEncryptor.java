package com.masabi.androidcipherperformance;

import android.support.annotation.NonNull;
import android.util.Log;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AESBytesEncryptor {

    private static final String TAG = AESBytesEncryptor.class.getSimpleName();

    @NonNull
    private final Cipher cipherEncrypt;

    /** The key to use for encryption. */
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
            Log.e(TAG, message);
            throw new CryptoException(message, ex);
        }
    }

    @NonNull
    public byte[] encrypt(@NonNull byte[] inputBytes,
                          @NonNull byte[] initVectorBytes) throws CryptoException {
        try {
            AlgorithmParameterSpec params = getAlgorithmParameterSpec(initVectorBytes);

            synchronized (cipherEncrypt) {
                cipherEncrypt.init(Cipher.ENCRYPT_MODE, secretKey, params);
                return cipherEncrypt.doFinal(inputBytes);
            }
        } catch (Exception ex) {
            String message = "Failed encryption";
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
