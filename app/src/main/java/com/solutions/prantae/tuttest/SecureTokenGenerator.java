package com.solutions.prantae.tuttest;

import android.widget.Toast;

import java.security.SecureRandom;

public class SecureTokenGenerator {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789`~!@#$%^&*()_+{}[]:,./<>?";

    // 2048 bit keys should be secure until 2030
    private static final int SECURE_TOKEN_LENGTH = 256;

    private static final SecureRandom random = new SecureRandom();

    private static final char[] symbols = CHARACTERS.toCharArray();

    private static final char[] buf = new char[SECURE_TOKEN_LENGTH];

    /**
     * Generate the next secure random token in the series.
     */
    public static String nextToken() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);

    }
}
