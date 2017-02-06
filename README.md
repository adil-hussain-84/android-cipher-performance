# Android Cipher Performance
This Android application demonstrates how encrypting and decrypting data using a **SecretKey** that is retrieved from the **Android KeyStore** is 5 times slower on the **Google Pixel** device running Android Nougat as compared to other Android devices running Android Nougat or Android Marshmallow (e.g. Samsung Galaxy S7, LG Nexus 5X).
<p>
In the application there are buttons to encrypt and decrypt an array of 999 random bytes using "AES/CBC/PKCS7PADDING" encryption with a key of size 256 bits and an initialization vector of size 128 bits. You will observe that the encrypt and decrypt operations take 100+ milliseconds on a Google Pixel device but only ~20 milliseconds on other Android devices.
<p>
By turning on Method Tracing using the Android Monitor tool in Android Studio you will see that the problem is in the `android.os.BinderProxy.transactNative` method call which follows from a call to `javax.crypto.Cipher.init`. Furhter details and discussion of the issue can be found [here](https://code.google.com/p/android/issues/detail?id=233337).
