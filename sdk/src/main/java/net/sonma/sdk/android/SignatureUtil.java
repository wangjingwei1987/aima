/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package net.sonma.sdk.android;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

class SignatureUtil {

    private final static char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    static String macSignature(String text, String secretKey) throws InvalidKeyException, NoSuchAlgorithmException {
        return hexEncode(hmac(text.getBytes(Charset.forName("utf-8")), secretKey.getBytes(Charset.forName("utf-8"))));
    }

    private static byte[] hmac(byte[] text, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
        String algorithm = "HmacSHA1";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(text);
    }


    private static String hexEncode(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = HEX[(0xF0 & data[i]) >>> 4];
            out[j++] = HEX[0x0F & data[i]];
        }
        return new String(out);
    }

    static String sha1AsHex(String data) {
        byte[] dataBytes = getDigest("SHA1").digest(data.getBytes(Charset.forName("utf-8")));
        return hexEncode(dataBytes);
    }


    static String encodeRFC3986(String str) {
        String result;

        try {
            result = URLEncoder.encode(str, "UTF-8")
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e) {
            result = str;
        }

        return result;
    }


    @SuppressWarnings("SameParameterValue")
    private static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Could not find MessageDigest with algorithm \"" + algorithm + "\"", ex);
        }
    }


}