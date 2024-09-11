/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.util.encryption;

import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.InvalidKeySpecException;

public class AESEncryptionUtil {

    private static final IvParameterSpec INIITAL_VECTOR = new IvParameterSpec(new byte[] { 42, 49, 55, 1, 99, 62, 70, 83, 104, 115, 8, 15, 91, 120, 27, 33 });

    private static final byte[] SALT = "xOtlS4mV9e0o89$LsetrR$R89u".getBytes(StandardCharsets.UTF_8);

    public static String encrypt(final String key, final String data) throws InvalidKeySpecException {
        return EncryptionUtils.aes(INIITAL_VECTOR, EncryptionUtils.fromString(key, SALT)).encrypt(data);
    }

    public static String decrypt(final String key, final String data) throws InvalidKeySpecException {
        return EncryptionUtils.aes(INIITAL_VECTOR, EncryptionUtils.fromString(key, SALT)).decrypt(data);
    }

}
