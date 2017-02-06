package com.masabi.androidcipherperformance;

import android.support.annotation.NonNull;

import java.security.KeyStore;

/**
 * Provides an instance of {@link KeyStoreProxy} using which the application can save cryptographic keys.
 */
public class KeyStoreProvider {

    /**
     * @return the {@link KeyStoreProxy} instance using which the application can save cryptographic keys.
     * @throws Exception the Android KeyStore is known to throw any exception under the sun!
     */
    @NonNull
    public KeyStoreProxy provide() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        return new KeyStoreProxy(keyStore);
    }
}
