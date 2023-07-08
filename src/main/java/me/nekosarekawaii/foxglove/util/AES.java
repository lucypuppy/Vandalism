package me.nekosarekawaii.foxglove.util;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class AES {

    private static final Cipher cipher;
    private static final SecretKeyFactory factory;
    private static final IvParameterSpec ivParameterSpec;
    private static final byte[] hwid;

    public static String encrypt(final String input, final SecretKey key) throws IllegalBlockSizeException, BadPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException {
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        return Base64.getEncoder().encodeToString(cipher.doFinal(input.getBytes()));
    }

    public static String decrypt(final String cipherText, final SecretKey key) throws InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException {
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
    }

    public static SecretKey getKeyFromPassword(final String password) throws InvalidKeySpecException {
        return new SecretKeySpec(factory.generateSecret(new PBEKeySpec(password.toCharArray(), hwid,
                65536, 256)).getEncoded(), "AES");
    }

    static {
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            ivParameterSpec = new IvParameterSpec(new byte[]{
                    42, 49, 55, 1, 99, 62, 70, 83, 104, 115, 8, 15, 91, 120, 27, 33
            });

            //Idk if this works on linux or mac os
            hwid = System.getenv("PROCESSOR_IDENTIFIER").getBytes();
        } catch (final NoSuchAlgorithmException | NoSuchPaddingException exception) {
            throw new RuntimeException("Failed to initialize AES", exception);
        }
    }

}
