package re.catgirls.irc.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import re.catgirls.irc.ChatClient;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <h2>Helper for the encryption used in our protocol</h2>
 * <p>
 * This class contains methods to encrypt and decrypt bytebuffers using RSA and AES
 * It also contains methods to generate RSA key pairs
 * </p>
 *
 * @author Lucy Luna
 */
public class EncryptionHelper {

    /**
     * Generates a new RSA key pair
     *
     * @return the key pair
     */
    public static KeyPair generateKeyPair() {
        try {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate key pair!", e);
        }
    }

    /**
     * Decrypts a bytebuffer using RSA and AES
     *
     * @param privateKey rsa private key to decrypt with
     * @param buf        bytebuffer to decrypt
     * @return decrypted bytebuffer
     * @throws Exception if something goes wrong
     */
    public static ByteBuf decryptByteBuf(final PrivateKey privateKey, final ByteBuf buf) throws Exception {
        final ByteBuf targetBuffer = Unpooled.buffer();

        byte[] ivBytes = new byte[buf.readInt()];
        buf.readBytes(ivBytes);
        ivBytes = decrypt(privateKey, ivBytes);

        byte[] keyBytes = new byte[buf.readInt()];
        buf.readBytes(keyBytes);
        keyBytes = decrypt(privateKey, keyBytes);

        // create specs
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        Cipher decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

        // wow decrypt
        byte[] encryptedData = new byte[buf.readableBytes()];
        buf.readBytes(encryptedData);

        return Unpooled.wrappedBuffer(decryptCipher.doFinal(encryptedData));
    }

    /**
     * Encrypts a bytebuffer using RSA and AES
     *
     * @param publicKey rsa public key to encrypt with
     * @param buf       bytebuffer to encrypt
     * @return encrypted bytebuffer
     * @throws Exception if something goes wrong
     */
    public static ByteBuf encryptByteBuf(final PublicKey publicKey, final ByteBuf buf) throws Exception {
        final ByteBuf finalBytebuf = Unpooled.buffer();

        // generate init vector key
        byte[] ivBytes = new byte[16];
        ThreadLocalRandom.current().nextBytes(ivBytes);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        // init key generator
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyGenerator.generateKey().getEncoded(), "AES");

        // write keys
        final byte[] ivBytesEncrypted = encrypt(publicKey, ivBytes);
        finalBytebuf.writeInt(ivBytesEncrypted.length);
        finalBytebuf.writeBytes(ivBytesEncrypted);

        final byte[] keySpec = encrypt(publicKey, secretKeySpec.getEncoded());
        finalBytebuf.writeInt(keySpec.length);
        finalBytebuf.writeBytes(keySpec);

        // encrypt our final traffic w AES
        Cipher encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
        finalBytebuf.writeBytes(Unpooled.wrappedBuffer(encryptCipher.doFinal(buf.array())));

        return finalBytebuf;
    }

    /**
     * Decrypts a byte array using RSA
     *
     * @param privateKey rsa private key to decrypt with
     * @param buf        byte array to decrypt
     * @return decrypted byte array
     * @throws Exception if something goes wrong
     */
    private static byte[] decrypt(final PrivateKey privateKey, final byte[] buf) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(buf);
    }

    /**
     * Encrypts a byte array using RSA
     *
     * @param publicKey rsa public key to encrypt with
     * @param buf       bytebuffer to encrypt
     * @return encrypted byte array
     * @throws Exception if something goes wrong
     */
    private static byte[] encrypt(final PublicKey publicKey, final byte[] buf) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(buf);
    }
}
