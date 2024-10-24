package com.desolitech.summary.domian.utils;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class Utils {

    public String base64(byte[] input) {
        byte[] encodedBytes = (Base64.getEncoder()).encode(input);
        return new String(encodedBytes);
    }

    public String base64(String input) {
        byte[] encodedBytes = (Base64.getEncoder()).encode(input.getBytes());
        return new String(encodedBytes);
    }

    public byte[] sha1(String input) throws NoSuchAlgorithmException {
        byte[] decodedBytes = Base64.getDecoder().decode("U0hBMQ=="); // Encoding Base64 for Kiuwan
        String decodedString = new String(decodedBytes);
        MessageDigest mDigest = MessageDigest.getInstance(decodedString);
        return mDigest.digest(input.getBytes());
    }

    public String objectToString(Object object) {
        var gson = new Gson();
        return gson.toJson(object);
    }

    public <T> T stringToObject(String object, Class<T> classOfT) {
        var gson = new Gson();
        return gson.fromJson(object, classOfT);
    }

    public String readException(Exception ex) {
        return new StringBuilder()
                .append(ex.getMessage())
                .append(" ON ")
                .append(ex.getStackTrace()[0].getFileName())
                .append(" LINE ")
                .append(ex.getStackTrace()[0].getLineNumber())
                .append(" [")
                .append(ex.getClass().getName())
                .append("]")
                .toString();
    }

    public String sha1ByteArrayToString(byte[] b) {
        StringBuilder result = new StringBuilder();
        for (byte value : b) {
            result.append(Integer.toString((value & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }
}
