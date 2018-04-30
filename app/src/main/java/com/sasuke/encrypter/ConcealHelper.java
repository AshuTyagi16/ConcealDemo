package com.sasuke.encrypter;

/**
 * Created by abc on 4/19/2018.
 */

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.facebook.android.crypto.keychain.AndroidConceal;
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.CryptoConfig;
import com.facebook.crypto.Entity;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class ConcealHelper {

    private static volatile ConcealHelper instance;

    private ConcealHelper() {
    }

    private static final String TAG = "encryptcheck";
    public static final String PREFIX_E = "encrypt_", PREFIX_D = "decrypt_";
    private static final String DEFAULT_ENCODE = "utf-8";

    private static SharedPrefsBackedKeyChain msp;
    private static Crypto mCrypto;
    private static Entity mEntity;

    public static ConcealHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (ConcealHelper.class) {
                if (instance == null) {
                    instance = new ConcealHelper();
                    instance.init(context);
                }
            }
        }
        return instance;
    }

    private void init(Context context) {
        if (null == mCrypto) {
            msp = new SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256);
            mCrypto = AndroidConceal.get().createDefaultCrypto(msp);
            String secretKey = new SimpleKeyGenerator(256).nextString();  //TODO:GENERATE SECRET KEY
            mEntity = Entity.create(secretKey);
        }
    }

    private byte[] encryptByte(byte[] plainBytes) {
        if (!mCrypto.isAvailable()) {
            Log.e("system.out", "encryptByte error: mCrypto is unavailable");
            return null;
        }

        if (null == plainBytes || plainBytes.length <= 0) {
            Log.e("system.out", "encryptByte error: plainBytes is null or length <= 0");
            return null;
        }

        try {
            byte[] result = mCrypto.encrypt(plainBytes, mEntity);
            if (null == result || result.length == 0) {
                Log.e("system.out", "encryptByte error: result is null or length <= 0");
                return null;
            }
            return result;
        } catch (KeyChainException e) {
            e.printStackTrace();
        } catch (CryptoInitializationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] decryptByte(byte[] encryptBytes) {
        if (!mCrypto.isAvailable()) {
            Log.e("system.out", "decryptByte error: mCrypto is unavailable");
            return null;
        }

        if (null == encryptBytes || encryptBytes.length <= 0) {
            Log.e("system.out", "decryptByte error: encryptBytes is null or length <= 0");
            return null;
        }

        try {
            byte[] result = mCrypto.decrypt(encryptBytes, mEntity);
            if (null == result || result.length == 0) {
                Log.e("system.out", "decryptByte error: result is null or length <= 0");
                return null;
            }
            return result;
        } catch (KeyChainException e) {
            e.printStackTrace();
        } catch (CryptoInitializationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String encryptString(String plainText) {
        if (TextUtils.isEmpty(plainText)) {
            Log.e("system.out", "encryptString error: plainText is empty");
            return null;
        }

        try {
            byte[] plainTextBytes = plainText.getBytes(DEFAULT_ENCODE);
            byte[] result = encryptByte(plainTextBytes);
            if (null == result || result.length <= 0) {
                Log.e("system.out", "encryptString error: encrypt result is null or length <= 0");
                return null;
            }
            String encryptText = Base64.encodeToString(result, Base64.DEFAULT);

            return encryptText;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;

    }

    private String decryptString(String encryptText) {
        if (TextUtils.isEmpty(encryptText)) {
            Log.e("system.out", "decryptString error: encryptText is empty");
            return null;
        }

        byte[] encryptTextBytes = Base64.decode(encryptText, Base64.DEFAULT);
        byte[] data = decryptByte(encryptTextBytes);
        if (null == data || data.length <= 0) {
            Log.e("system.out", "decryptString error: decrypt result is null or length <= 0");
            return null;
        }
        try {
            String plainText = new String(data, DEFAULT_ENCODE);
            return plainText;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;

    }

    public File encryptFile(File file) {
        if (!mCrypto.isAvailable()) {
            return null;
        }

        if (null == file) {
            return null;
        }

        String originFilePath = file.getAbsolutePath();
        String encryptFilePath = String.format("%s%s%s%s", file.getParent(), File.separator, PREFIX_E, file.getName());

        File encryptFile = new File(encryptFilePath);
        if (encryptFile.exists()) {
            encryptFile.deleteOnExit();
        }

        try {
            FileInputStream sourceFile = new FileInputStream(originFilePath);

            OutputStream fileOS = new BufferedOutputStream(new FileOutputStream(encryptFile));
            OutputStream out = mCrypto.getCipherOutputStream(fileOS, mEntity);

            int read = 0;
            byte[] buffer = new byte[1024];
            while ((read = sourceFile.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            if (file.exists()) {
                String name = file.getName();
                if (file.delete())
                    Log.d(TAG, "original file " + name + " DELETED");
            }

            out.flush();
            fileOS.flush();
            out.close();
            fileOS.close();

            sourceFile.close();

            return encryptFile;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CryptoInitializationException e) {
            e.printStackTrace();
        } catch (KeyChainException e) {
            e.printStackTrace();
        }

        return null;
    }

    public File decryptFile(File file) {
        if (!mCrypto.isAvailable()) {
            return null;
        }

        if (null == file) {
            return null;
        }

        String fileName = file.getName();
        fileName = fileName.substring(PREFIX_E.length(), fileName.length());
        String originFilePath = file.getAbsolutePath();
        String decryptFilePath = String.format("%s%s%s%s", file.getParent(), File.separator, PREFIX_D, fileName);

        File decryptFile = new File(decryptFilePath);
        if (decryptFile.exists()) {
            decryptFile.deleteOnExit();
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(originFilePath);
            InputStream inputStream = mCrypto.getCipherInputStream(fileInputStream, mEntity);

            OutputStream out = new BufferedOutputStream(new FileOutputStream(decryptFile));
            int read = 0;
            byte[] buffer = new byte[1024];
            while ((read = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            out.flush();
            out.close();

            inputStream.close();
            fileInputStream.close();

            if (file.exists()) {
                String name = file.getName();
                if (file.delete())
                    Log.d(TAG, "original file " + name + " DELETED");
            }
            return decryptFile;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CryptoInitializationException e) {
            e.printStackTrace();
        } catch (KeyChainException e) {
            e.printStackTrace();
        }

        return null;
    }

    public InputStream decryptFileAndConvertIntoStream(File file) {
        if (!mCrypto.isAvailable()) {
            return null;
        }

        if (null == file) {
            return null;
        }

        String fileName = file.getName();
        fileName = fileName.substring(PREFIX_E.length(), fileName.length());
        String originFilePath = file.getAbsolutePath();
        String decryptFilePath = String.format("%s%s%s%s", file.getParent(), File.separator, PREFIX_D, fileName);

        File decryptFile = new File(decryptFilePath);
        if (decryptFile.exists()) {
            decryptFile.deleteOnExit();
        }

        FileInputStream fileInputStream = null;
        InputStream inputStream = null;
        try {
            fileInputStream = new FileInputStream(originFilePath);
            inputStream = mCrypto.getCipherInputStream(fileInputStream, mEntity);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (KeyChainException e) {
            e.printStackTrace();
        } catch (CryptoInitializationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }
}
