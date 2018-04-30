package com.sasuke.encrypter;

import android.app.Application;

import com.facebook.soloader.SoLoader;

/**
 * Created by abc on 4/13/2018.
 */

public class Encrypter extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SoLoader.init(this, false);
    }
}
