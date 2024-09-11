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

package de.nekosarekawaii.vandalism.util.encryption.impl;

import de.nekosarekawaii.vandalism.util.encryption.Encryptor;

public class Rot13 implements Encryptor {

    private final int offset;

    public Rot13(int offset) {
        this.offset = offset;
    }

    @Override
    public byte[] encrypt(byte[] data) {
        StringBuilder result = new StringBuilder();

        for (char c : new String(data).toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isLowerCase(c) ? 'a' : 'A';
                result.append((char) (base + (c - base + offset) % 26));
            } else {
                result.append(c);
            }
        }

        return result.toString().getBytes();
    }

    @Override
    public byte[] decrypt(byte[] data) {
        // Rot13 is its own inverse, so decrypting is the same as encrypting
        return encrypt(data);
    }

}