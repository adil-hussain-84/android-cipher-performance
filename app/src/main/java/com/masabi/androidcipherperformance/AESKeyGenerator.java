package com.masabi.androidcipherperformance;

import android.support.annotation.NonNull;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Provides methods for generating AES keys.
 */
public class AESKeyGenerator {

    @NonNull
    private final KeyGenerator keyGenerator;

    /**
     * Constructor.
     *
     * @param keySizeBits the size of the keys to generate with this {@link AESKeyGenerator} instance.
     *                    (This should be a size that makes sense for AES encryption, e.g. 128-bits, 256-bits etc.)
     * @throws CryptoException if there was a problem constructing the {@link AESKeyGenerator} instance.
     */
    public AESKeyGenerator(int keySizeBits) throws CryptoException {
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(keySizeBits);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("Failed instantiating AES key generator", e);
        }
    }

    /**
     * Generates a random {@link SecretKey} instance suitable for AES encryption and decryption.
     *
     * @return the generated secret key.
     */
    public SecretKey generateSecretKey() {
        return keyGenerator.generateKey();
    }
}
