package de.vandalismdevelopment.vandalism.util;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class EncryptionUtil {

    private final static Cipher CIPHER;
    private final static SecretKeyFactory FACTORY;
    private final static IvParameterSpec IV_PARAMETER_SPEC;
    private final static byte[] HWID;

    public static String encrypt(final String input, final SecretKey key) throws IllegalBlockSizeException, BadPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException {
        CIPHER.init(Cipher.ENCRYPT_MODE, key, IV_PARAMETER_SPEC);
        return Base64.getEncoder().encodeToString(CIPHER.doFinal(input.getBytes()));
    }

    public static String decrypt(final String cipherText, final SecretKey key) throws InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException {
        CIPHER.init(Cipher.DECRYPT_MODE, key, IV_PARAMETER_SPEC);
        return new String(CIPHER.doFinal(Base64.getDecoder().decode(cipherText)));
    }

    public static SecretKey getKeyFromPassword(final String password) throws InvalidKeySpecException {
        return new SecretKeySpec(FACTORY.generateSecret(new PBEKeySpec(password.toCharArray(), HWID,
                65536, 256)).getEncoded(), "AES");
    }

    static {
        try {
            CIPHER = Cipher.getInstance("AES/CBC/PKCS5Padding");
            FACTORY = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            IV_PARAMETER_SPEC = new IvParameterSpec(new byte[]{
                    42, 49, 55, 1, 99, 62, 70, 83, 104, 115, 8, 15, 91, 120, 27, 33
            });
            HWID = System.getenv("PROCESSOR_IDENTIFIER").getBytes();
        } catch (final NoSuchAlgorithmException | NoSuchPaddingException exception) {
            throw new RuntimeException("Failed to initialize AES", exception);
        }
    }

}
