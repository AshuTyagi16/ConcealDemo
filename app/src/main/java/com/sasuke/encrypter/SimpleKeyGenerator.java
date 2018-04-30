package com.sasuke.encrypter;

import android.content.Context;
import android.util.Base64;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by abc on 4/23/2018.
 */

public class SimpleKeyGenerator {
    private static final String symbols = "AskIITians";

    private final Random random = new SecureRandom();

    private final char[] buf;

    public SimpleKeyGenerator(int length)
    {
        if (length < 1)
            throw new IllegalArgumentException("length < 1: " + length);
        buf = new char[length];
    }

    public String nextString()
    {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols.charAt(random.nextInt(symbols.length()));
        return new String(buf);
    }

}
