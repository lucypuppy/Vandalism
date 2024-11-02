/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.util.encryption.impl.aes;

import de.nekosarekawaii.vandalism.util.encryption.Encryptor;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AES implements Encryptor {

    private static final Cipher CIPHER;

    private final IvParameterSpec initialVector;
    private final SecretKey secretKey;

    public AES(final IvParameterSpec initialVector, final SecretKey secretKey) {
        this.initialVector = initialVector;
        this.secretKey = secretKey;
    }

    static {
        try {
            CIPHER = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] encrypt(byte[] data) {
        try {
            CIPHER.init(Cipher.ENCRYPT_MODE, secretKey, initialVector);
            return Base64.getEncoder().encode(CIPHER.doFinal(data));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public byte[] decrypt(byte[] data) {
        try {
            CIPHER.init(Cipher.DECRYPT_MODE, secretKey, initialVector);
            return CIPHER.doFinal(Base64.getDecoder().decode(data));
        } catch (Exception e) {
            return null;
        }
    }

}