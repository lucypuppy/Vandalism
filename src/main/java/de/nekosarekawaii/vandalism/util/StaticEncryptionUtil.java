package de.nekosarekawaii.vandalism.util;

import de.florianmichael.rclasses.io.encryption.EncryptionUtils;

import javax.crypto.spec.IvParameterSpec;
import java.security.spec.InvalidKeySpecException;

public class StaticEncryptionUtil {

    private static final IvParameterSpec INIITAL_VECTOR = new IvParameterSpec(new byte[] { 42, 49, 55, 1, 99, 62, 70, 83, 104, 115, 8, 15, 91, 120, 27, 33 });

    public static String encrypt(final String key, final String data) throws InvalidKeySpecException {
        return EncryptionUtils.aes(INIITAL_VECTOR, EncryptionUtils.fromString(key)).encrypt(data);
    }

    public static String decrypt(final String key, final String data) throws InvalidKeySpecException {
        return EncryptionUtils.aes(INIITAL_VECTOR, EncryptionUtils.fromString(key)).decrypt(data);
    }

}
