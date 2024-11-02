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

package de.nekosarekawaii.vandalism.util.render.gl.utils;

import net.lenni0451.reflect.JavaBypass;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;
import sun.misc.Unsafe;

public class TemporaryValues {

    public static final Unsafe UNSAFE = JavaBypass.getUnsafe();
    public static final Vector2f VECTOR2F = new Vector2f();
    public static final Vector3f VECTOR3F = new Vector3f();
    public static final Vector4f VECTOR4F = new Vector4f();
    public static final Vector3d VECTOR3D = new Vector3d();
    public static final Vector4d VECTOR4D = new Vector4d();
    public static final Matrix4f IDENTITY_MATRIX4F = new Matrix4f().identity();
}
