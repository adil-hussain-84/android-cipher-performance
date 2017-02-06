package com.masabi.androidcipherperformance;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import javax.crypto.SecretKey;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_ALIAS = "MySecretKey";

    @BindView(R.id.textViewResult)
    TextView textViewResult;

    private byte[] unencryptedData;
    private byte[] encryptedData;
    private byte[] initVector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.buttonCreateSecretKey)
    void onCreateSecretKeyClick() {
        new CreateSecretKeyAsyncTask().execute();
    }

    @OnClick(R.id.buttonEncryptData)
    void onEncryptDataClick() {
        new EncryptDataAsyncTask().execute();
    }

    @OnClick(R.id.buttonDecryptData)
    void onDecryptDataClick() {
        new DecryptDataAsyncTask().execute();
    }

    /**
     * @return the {@link SecretKey} instance in the Android KeyStore
     * which this app will use for encryption and decryption.
     * @throws CryptoException if the {@link SecretKey} instance could not be retrieved.
     */
    private SecretKey getSecretKey() throws CryptoException {
        try {
            KeyStoreProxy keyStoreProxy = new KeyStoreProvider().provide();
            Key key = keyStoreProxy.getKey(KEY_ALIAS);

            if (key == null) {
                throw new CryptoException("Failed getting secret key from the Android KeyStore");
            } else if (key instanceof SecretKey) {
                return (SecretKey) key;
            } else {
                throw new CryptoException("Retrieved KeyStore entry but it's not of the expected type");
            }
        } catch (Exception e) {
            throw new CryptoException("Failed getting secret key", e);
        }
    }

    /**
     * Creates a {@link SecretKey} instance and saves it in the Android KeyStore.
     */
    private class CreateSecretKeyAsyncTask extends AsyncTask<Void, Void, Void> {

        private Long executionTimeMillis;

        @Override
        protected void onPreExecute() {
            textViewResult.setText("Creating secret key...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                long startTimeMillis = new Date().getTime();

                AESKeyGenerator secretKeyGenerator = new AESKeyGenerator(256);
                SecretKey secretKey = secretKeyGenerator.generateSecretKey();

                KeyStoreProxy keyStoreProxy = new KeyStoreProvider().provide();
                keyStoreProxy.setEntry(KEY_ALIAS, secretKey);

                long endTimeMillis = new Date().getTime();
                executionTimeMillis = endTimeMillis - startTimeMillis;
            } catch (Exception e) {
                executionTimeMillis = null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (executionTimeMillis == null) {
                textViewResult.setText("Failed creating secret key.");
            } else {
                textViewResult.setText(String.format("Created secret key in %d milliseconds", executionTimeMillis));
            }
        }
    }

    /**
     * Encrypts a random array of 999 bytes using the {@link SecretKey} instance
     * that is saved in the Android Keystore.
     */
    private class EncryptDataAsyncTask extends AsyncTask<Void, Void, Void> {

        private Long executionTimeMillis;

        @Override
        protected void onPreExecute() {
            textViewResult.setText("Encrypting data...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                SecretKey secretKey = getSecretKey();

                long startTimeMillis = new Date().getTime();

                unencryptedData = new byte[999];
                Random dataGenerator = new Random();
                dataGenerator.nextBytes(unencryptedData);

                AESKeyGenerator initVectorGenerator = new AESKeyGenerator(128);;
                initVector = initVectorGenerator.generateSecretKey().getEncoded();

                AESBytesEncryptor aesBytesEncryptor = new AESBytesEncryptor(secretKey);
                encryptedData = aesBytesEncryptor.encrypt(unencryptedData, initVector);

                long endTimeMillis = new Date().getTime();
                executionTimeMillis = endTimeMillis - startTimeMillis;
            } catch (CryptoException e) {
                executionTimeMillis = null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (executionTimeMillis == null) {
                textViewResult.setText("Failed encrypting data.");
            } else {
                textViewResult.setText(String.format("Encrypted data in %d milliseconds", executionTimeMillis));
            }
        }
    }

    /**
     * Decrypts the encrypted data using the {@link SecretKey} instance
     * that is saved in the Android Keystore.
     */
    private class DecryptDataAsyncTask extends AsyncTask<Void, Void, Void> {

        private Long executionTimeMillis;

        @Override
        protected void onPreExecute() {
            textViewResult.setText("Decrypting data...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                SecretKey secretKey = getSecretKey();

                long startTimeMillis = new Date().getTime();

                AESBytesDecryptor aesBytesDecryptor = new AESBytesDecryptor(secretKey);
                byte[] decryptedData = aesBytesDecryptor.decrypt(encryptedData, initVector);

                if (!Arrays.equals(unencryptedData, decryptedData)) {
                    throw new CryptoException("Unencrypted data and Decrypted data do not match.");
                }

                long endTimeMillis = new Date().getTime();
                executionTimeMillis = endTimeMillis - startTimeMillis;
            } catch (CryptoException e) {
                executionTimeMillis = null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (executionTimeMillis == null) {
                textViewResult.setText("Failed decrypting data.");
            } else {
                textViewResult.setText(String.format("Decrypted data in %d milliseconds", executionTimeMillis));
            }
        }
    }
}
