package com.masabi.androidcipherperformance;

import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.security.Key;
import java.security.KeyStore;

import javax.crypto.SecretKey;

/**
 * A proxy for the {@link KeyStore} class.
 */
public class KeyStoreProxy {

    @NonNull
    private final KeyStore keyStore;

    public KeyStoreProxy(@NonNull KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    @Nullable
    public Key getKey(@NonNull String alias) throws Exception {
        return keyStore.getKey(alias, null);
    }

    public void setEntry(@NonNull String alias,
                         @NonNull SecretKey secretKey) throws Exception {
        KeyStore.Entry entry = new KeyStore.SecretKeyEntry(secretKey);
        KeyStore.ProtectionParameter protectionParameter = getSecretKeyProtectionParameter();

        keyStore.setEntry(alias, entry, protectionParameter);
    }

    @NonNull
    private KeyStore.ProtectionParameter getSecretKeyProtectionParameter() {
        return new KeyProtection.Builder(KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setRandomizedEncryptionRequired(false)
                .setUserAuthenticationRequired(false)
                .build();
    }
}
