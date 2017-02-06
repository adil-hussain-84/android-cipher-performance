package com.masabi.androidcipherperformance;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import javax.crypto.SecretKey;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int NUM_ITEMS = 20;

    @BindView(R.id.textViewResult)
    TextView textViewResult;

    private byte[][] unencryptedDataList;
    private byte[][] encryptedDataList;
    private byte[][] initVectorList;
    private SecretKey[] secretKeyList;

    private AESKeyGenerator secretKeyGenerator;
    private AESKeyGenerator initVectorGenerator;
    private Random dataGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        unencryptedDataList = new byte[NUM_ITEMS][];
        encryptedDataList = new byte[NUM_ITEMS][];
        initVectorList = new byte[NUM_ITEMS][];
        secretKeyList = new SecretKey[NUM_ITEMS];
    }

    @OnClick(R.id.buttonEncryptData)
    void onEncryptDataClick() {
        new EncryptDataAsyncTask().execute();
    }

    @OnClick(R.id.buttonDecryptData)
    void onDecryptDataClick() {
        new DecryptDataAsyncTask().execute();
    }

    private AESKeyGenerator getSecretKeyGenerator() throws CryptoException {
        if (secretKeyGenerator == null) {
            secretKeyGenerator = new AESKeyGenerator(256);
        }

        return secretKeyGenerator;
    }

    private AESKeyGenerator getInitVectorGenerator() throws CryptoException {
        if (initVectorGenerator == null) {
            initVectorGenerator = new AESKeyGenerator(128);
        }

        return initVectorGenerator;
    }

    private Random getDataGenerator() {
        if (dataGenerator == null) {
            dataGenerator = new Random();
        }

        return dataGenerator;
    }

    private class EncryptDataAsyncTask extends AsyncTask<Void, Void, Void> {

        private Long executionTimeMillis;

        @Override
        protected void onPreExecute () {
            textViewResult.setText("Encrypting data...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                long startTimeMillis = new Date().getTime();

                AESKeyGenerator secretKeyGenerator = getSecretKeyGenerator();
                AESKeyGenerator initVectorGenerator = getInitVectorGenerator();
                Random dataGenerator = getDataGenerator();

                for (int i = 0; i < NUM_ITEMS; i++) {
                    byte[] unencryptedData = new byte[9999];
                    dataGenerator.nextBytes(unencryptedData);

                    byte[] initVector = initVectorGenerator.generateSecretKey().getEncoded();
                    SecretKey secretKey = secretKeyGenerator.generateSecretKey();

                    AESBytesEncryptor aesBytesEncryptor = new AESBytesEncryptor(secretKey);
                    byte[] encryptedData = aesBytesEncryptor.encrypt(unencryptedData, initVector);

                    unencryptedDataList[i] = unencryptedData;
                    encryptedDataList[i] = encryptedData;
                    initVectorList[i] = initVector;
                    secretKeyList[i] = secretKey;
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
                textViewResult.setText("Failed encrypting data.");
            } else {
                textViewResult.setText(String.format("Encrypted data in %d milliseconds", executionTimeMillis));
            }
        }
    }

    private class DecryptDataAsyncTask extends AsyncTask<Void, Void, Void> {

        private Long executionTimeMillis;

        @Override
        protected void onPreExecute () {
            textViewResult.setText("Decrypting data...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                long startTimeMillis = new Date().getTime();

                for (int i = 0; i < NUM_ITEMS; i++) {
                    byte[] unencryptedData = unencryptedDataList[i];
                    byte[] encryptedData = encryptedDataList[i];
                    byte[] initVector = initVectorList[i];
                    SecretKey secretKey = secretKeyList[i];

                    AESBytesDecryptor aesBytesDecryptor = new AESBytesDecryptor(secretKey);
                    byte[] decryptedData = aesBytesDecryptor.decrypt(encryptedData, initVector);

                    if (!Arrays.equals(unencryptedData, decryptedData)) {
                        throw new CryptoException("Unencrypted data and Decrypted data do not match.");
                    }
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
