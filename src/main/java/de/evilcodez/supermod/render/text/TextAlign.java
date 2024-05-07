/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

<<<<<<<<HEAD:src/main/java/de/nekosarekawaii/vandalism/util/encryption/Base64.java
package de.nekosarekawaii.vandalism.util.encryption;

public class Base64 implements Encryptor {

    @Override
    public byte[] encrypt(byte[] data) {
        return java.util.Base64.getEncoder().encode(data);
    }

    @Override
    public byte[] decrypt(byte[] data) {
        return java.util.Base64.getDecoder().decode(data);
    }

}
========
package de.evilcodez.supermod.render.text;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TextAlign {
    X_POSITIVE(false),
    X_NEGATIVE(false),
    X_CENTER(false),
    Y_POSITIVE(true),
    Y_NEGATIVE(true),
    Y_CENTER(true);

    @Getter
    private final boolean vertical;
}
>>>>>>>>refs/heads/main:src/main/java/de/evilcodez/supermod/render/text/TextAlign.java
