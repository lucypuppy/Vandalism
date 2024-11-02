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

package de.nekosarekawaii.vandalism.util.render.gl.render;

import de.nekosarekawaii.vandalism.util.render.gl.utils.TemporaryValues;
import de.nekosarekawaii.vandalism.util.render.gl.vertex.DataType;
import org.joml.Matrix3dc;
import org.joml.Matrix3fc;
import org.joml.Matrix4dc;
import org.joml.Matrix4fc;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.Vector4d;
import org.joml.Vector4dc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.joml.Vector4i;
import org.joml.Vector4ic;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public interface AttribConsumer {

    AttribConsumer putBoolean(boolean b);

    AttribConsumer putByte(byte b);

    AttribConsumer putShort(short s);

    AttribConsumer putInt(int i);

    AttribConsumer putLong(long l);

    AttribConsumer putFloat(float f);

    AttribConsumer putDouble(double d);

    default AttribConsumer putBooleans(boolean... bs) {
        for (boolean b : bs) {
            this.putBoolean(b);
        }
        return this;
    }

    default AttribConsumer putBooleans(boolean[] booleans, int offset, int length) {
        for (int i = offset; i < offset + length; i++) {
            this.putBoolean(booleans[i]);
        }
        return this;
    }

    AttribConsumer putBytes(byte... bs);

    AttribConsumer putBytes(byte[] bytes, int offset, int length);

    AttribConsumer putBytes(ByteBuffer buffer);

    default AttribConsumer putHalf(float f) {
        this.putShort((short) DataType.floatToHalfBits(f));
        return this;
    }

    default AttribConsumer putShorts(short... ss) {
        for (short s : ss) {
            this.putShort(s);
        }
        return this;
    }

    default AttribConsumer putShorts(short[] shorts, int offset, int length) {
        for (int i = offset; i < offset + length; i++) {
            this.putShort(shorts[i]);
        }
        return this;
    }

    default AttribConsumer putInts(int... is) {
        for (int i : is) {
            this.putInt(i);
        }
        return this;
    }

    default AttribConsumer putInts(int[] ints, int offset, int length) {
        for (int i = offset; i < offset + length; i++) {
            this.putInt(ints[i]);
        }
        return this;
    }

    default AttribConsumer putLongs(long... ls) {
        for (long l : ls) {
            this.putLong(l);
        }
        return this;
    }

    default AttribConsumer putLongs(long[] longs, int offset, int length) {
        for (int i = offset; i < offset + length; i++) {
            this.putLong(longs[i]);
        }
        return this;
    }

    default AttribConsumer putFloats(float... fs) {
        for (float f : fs) {
            this.putFloat(f);
        }
        return this;
    }

    default AttribConsumer putFloats(float[] floats, int offset, int length) {
        for (int i = offset; i < offset + length; i++) {
            this.putFloat(floats[i]);
        }
        return this;
    }

    default AttribConsumer putDoubles(double... ds) {
        for (double d : ds) {
            this.putDouble(d);
        }
        return this;
    }

    default AttribConsumer putDoubles(double[] doubles, int offset, int length) {
        for (int i = offset; i < offset + length; i++) {
            this.putDouble(doubles[i]);
        }
        return this;
    }

    default AttribConsumer putHalfs(float... fs) {
        for (float f : fs) {
            this.putHalf(f);
        }
        return this;
    }

    default AttribConsumer putString(String s, Charset charset) {
        return this.putBytes(s.getBytes(charset));
    }

    default AttribConsumer putString(String s) {
        return this.putBytes(s.getBytes());
    }

    default AttribConsumer putColor8(int argb) {
        this.putByte((byte) ((argb >> 16) & 0xFF));
        this.putByte((byte) ((argb >> 8) & 0xFF));
        this.putByte((byte) (argb & 0xFF));
        this.putByte((byte) ((argb >> 24) & 0xFF));
        return this;
    }

    default AttribConsumer putColor8(int r, int g, int b, int a) {
        this.putByte((byte) r);
        this.putByte((byte) g);
        this.putByte((byte) b);
        this.putByte((byte) a);
        return this;
    }

    default AttribConsumer putColor8(float r, float g, float b, float a) {
        this.putByte((byte) (r * 255.0f));
        this.putByte((byte) (g * 255.0f));
        this.putByte((byte) (b * 255.0f));
        this.putByte((byte) (a * 255.0f));
        return this;
    }

    default AttribConsumer putColor8(Color color) {
        return this.putColor8(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    default AttribConsumer putColor8(Color color, float alpha) {
        return this.putColor8(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha);
    }

    default AttribConsumer putColor8(Color color, int alpha) {
        return this.putColor8(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    default AttribConsumer putColorF(int argb) {
        this.putFloat(((argb >> 16) & 0xFF) / 255.0f);
        this.putFloat(((argb >> 8) & 0xFF) / 255.0f);
        this.putFloat((argb & 0xFF) / 255.0f);
        this.putFloat(((argb >> 24) & 0xFF) / 255.0f);
        return this;
    }

    default AttribConsumer putColorF(int r, int g, int b, int a) {
        this.putFloat(r / 255.0f);
        this.putFloat(g / 255.0f);
        this.putFloat(b / 255.0f);
        this.putFloat(a / 255.0f);
        return this;
    }

    default AttribConsumer putColorF(float r, float g, float b, float a) {
        this.putFloat(r);
        this.putFloat(g);
        this.putFloat(b);
        this.putFloat(a);
        return this;
    }

    default AttribConsumer putColorF(Color color) {
        return this.putColorF(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    default AttribConsumer putColorF(Color color, float alpha) {
        return this.putColorF(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha);
    }

    default AttribConsumer putColorF(Color color, int alpha) {
        return this.putColorF(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    default AttribConsumer putVector2f(float x, float y) {
        this.putFloat(x);
        this.putFloat(y);
        return this;
    }

    // Texture coordinates

    default AttribConsumer putUV(float u, float v) {
        this.putFloat(u);
        this.putFloat(v);
        return this;
    }

    default AttribConsumer putUV(Vector2f vector) {
        return this.putUV(vector.x, vector.y);
    }

    default AttribConsumer putUV(Vector2fc vector) {
        return this.putUV(vector.x(), vector.y());
    }

    default AttribConsumer putUV(Vector2ic vector) {
        return this.putUV(vector.x(), vector.y());
    }

    // End texture coordinates

    default AttribConsumer putVector2f(Vector2f vector) {
        return this.putVector2f(vector.x, vector.y);
    }

    default AttribConsumer putVector2f(Vector2fc vector) {
        return this.putVector2f(vector.x(), vector.y());
    }

    default AttribConsumer putVector2i(int x, int y) {
        this.putInt(x);
        this.putInt(y);
        return this;
    }

    default AttribConsumer putVector2i(Vector2i vector) {
        return this.putVector2i(vector.x, vector.y);
    }

    default AttribConsumer putVector2i(Vector2ic vector) {
        return this.putVector2i(vector.x(), vector.y());
    }

    default AttribConsumer putVector2d(double x, double y) {
        this.putDouble(x);
        this.putDouble(y);
        return this;
    }

    default AttribConsumer putVector2d(Vector2d vector) {
        return this.putVector2d(vector.x, vector.y);
    }

    default AttribConsumer putVector2d(Vector2dc vector) {
        return this.putVector2d(vector.x(), vector.y());
    }

    // Position

    AttribConsumer pos(float x, float y, float z);

    default AttribConsumer pos(double x, double y, double z) {
        return this.pos((float) x, (float) y, (float) z);
    }

    default AttribConsumer pos(Vector3f vector) {
        return this.pos(vector.x, vector.y, vector.z);
    }

    default AttribConsumer pos(Vector3fc vector) {
        return this.pos(vector.x(), vector.y(), vector.z());
    }

    default AttribConsumer pos(Matrix4fc matrix, float x, float y, float z) {
        final Vector3f pos = TemporaryValues.VECTOR3F.set(x, y, z);
        return this.pos(matrix != null ? matrix.transformPosition(pos) : pos);
    }

    default AttribConsumer pos(Matrix4fc matrix, Vector3f vector) {
        return this.pos(matrix, vector.x, vector.y, vector.z);
    }

    default AttribConsumer pos(Matrix4fc matrix, Vector3fc vector) {
        return this.pos(matrix, vector.x(), vector.y(), vector.z());
    }

    default AttribConsumer pos(Matrix3fc matrix, float x, float y, float z) {
        return this.pos(matrix.transform(TemporaryValues.VECTOR3F.set(x, y, z)));
    }

    default AttribConsumer pos(Matrix3fc matrix, Vector3f vector) {
        return this.pos(matrix, vector.x, vector.y, vector.z);
    }

    default AttribConsumer pos(Matrix3fc matrix, Vector3fc vector) {
        return this.pos(matrix, vector.x(), vector.y(), vector.z());
    }

    default AttribConsumer pos(Matrix4fc matrix, double x, double y, double z) {
        return this.pos(matrix, (float) x, (float) y, (float) z);
    }

    AttribConsumer halfPos(float x, float y, float z);

    default AttribConsumer halfPos(double x, double y, double z) {
        return this.halfPos((float) x, (float) y, (float) z);
    }

    default AttribConsumer halfPos(Vector3f vector) {
        return this.halfPos(vector.x, vector.y, vector.z);
    }

    default AttribConsumer halfPos(Vector3fc vector) {
        return this.halfPos(vector.x(), vector.y(), vector.z());
    }

    default AttribConsumer halfPos(Matrix4fc matrix, float x, float y, float z) {
        return this.halfPos(matrix.transformPosition(TemporaryValues.VECTOR3F.set(x, y, z)));
    }

    default AttribConsumer halfPos(Matrix4fc matrix, Vector3f vector) {
        return this.halfPos(matrix, vector.x, vector.y, vector.z);
    }

    default AttribConsumer halfPos(Matrix4fc matrix, Vector3fc vector) {
        return this.halfPos(matrix, vector.x(), vector.y(), vector.z());
    }

    default AttribConsumer halfPos(Matrix3fc matrix, float x, float y, float z) {
        return this.halfPos(matrix.transform(TemporaryValues.VECTOR3F.set(x, y, z)));
    }

    default AttribConsumer halfPos(Matrix3fc matrix, Vector3f vector) {
        return this.halfPos(matrix, vector.x, vector.y, vector.z);
    }

    default AttribConsumer halfPos(Matrix3fc matrix, Vector3fc vector) {
        return this.halfPos(matrix, vector.x(), vector.y(), vector.z());
    }

    default AttribConsumer halfPos(Matrix4fc matrix, double x, double y, double z) {
        return this.halfPos(matrix, (float) x, (float) y, (float) z);
    }

    // End position

    default AttribConsumer putVector3f(float x, float y, float z) {
        this.putFloat(x);
        this.putFloat(y);
        this.putFloat(z);
        return this;
    }

    default AttribConsumer putVector3f(Matrix4fc matrix, float x, float y, float z) {
        final Vector4f temp = TemporaryValues.VECTOR4F;
        temp.set(x, y, z, 1.0f);
        matrix.transform(temp);
        this.putFloat(temp.x);
        this.putFloat(temp.y);
        this.putFloat(temp.z);
        return this;
    }

    default AttribConsumer putVector3f(Matrix3fc matrix, float x, float y, float z) {
        final Vector3f temp = TemporaryValues.VECTOR3F;
        temp.set(x, y, z);
        matrix.transform(temp);
        this.putFloat(temp.x);
        this.putFloat(temp.y);
        this.putFloat(temp.z);
        return this;
    }

    default AttribConsumer putVector3f(Vector3f vector) {
        return this.putVector3f(vector.x, vector.y, vector.z);
    }

    default AttribConsumer putVector3f(Matrix4fc matrix, Vector3f vector) {
        return this.putVector3f(matrix, vector.x, vector.y, vector.z);
    }

    default AttribConsumer putVector3f(Matrix3fc matrix, Vector3f vector) {
        return this.putVector3f(matrix, vector.x, vector.y, vector.z);
    }

    default AttribConsumer putVector3f(Vector3fc vector) {
        return this.putVector3f(vector.x(), vector.y(), vector.z());
    }

    default AttribConsumer putVector3f(Matrix4fc matrix, Vector3fc vector) {
        return this.putVector3f(matrix, vector.x(), vector.y(), vector.z());
    }

    default AttribConsumer putVector3f(Matrix3fc matrix, Vector3fc vector) {
        return this.putVector3f(matrix, vector.x(), vector.y(), vector.z());
    }

    default AttribConsumer putVector3i(int x, int y, int z) {
        this.putInt(x);
        this.putInt(y);
        this.putInt(z);
        return this;
    }

    default AttribConsumer putVector3i(Vector3i vector) {
        return this.putVector3i(vector.x, vector.y, vector.z);
    }

    default AttribConsumer putVector3i(Vector3ic vector) {
        return this.putVector3i(vector.x(), vector.y(), vector.z());
    }

    default AttribConsumer putVector3d(double x, double y, double z) {
        this.putDouble(x);
        this.putDouble(y);
        this.putDouble(z);
        return this;
    }

    default AttribConsumer putVector3d(Matrix4dc matrix, double x, double y, double z) {
        final Vector4d temp = TemporaryValues.VECTOR4D;
        temp.set(x, y, z, 1.0);
        matrix.transform(temp);
        this.putDouble(temp.x);
        this.putDouble(temp.y);
        this.putDouble(temp.z);
        return this;
    }

    default AttribConsumer putVector3d(Matrix3dc matrix, double x, double y, double z) {
        final Vector3d temp = TemporaryValues.VECTOR3D;
        temp.set(x, y, z);
        matrix.transform(temp);
        this.putDouble(temp.x);
        this.putDouble(temp.y);
        this.putDouble(temp.z);
        return this;
    }

    default AttribConsumer putVector3d(Vector3d vector) {
        return this.putVector3d(vector.x, vector.y, vector.z);
    }

    default AttribConsumer putVector3d(Matrix4dc matrix, Vector3d vector) {
        return this.putVector3d(matrix, vector.x, vector.y, vector.z);
    }

    default AttribConsumer putVector3d(Matrix3dc matrix, Vector3d vector) {
        return this.putVector3d(matrix, vector.x, vector.y, vector.z);
    }

    default AttribConsumer putVector3d(Vector3dc vector) {
        return this.putVector3d(vector.x(), vector.y(), vector.z());
    }

    default AttribConsumer putVector3d(Matrix4dc matrix, Vector3dc vector) {
        return this.putVector3d(matrix, vector.x(), vector.y(), vector.z());
    }

    default AttribConsumer putVector4f(float x, float y, float z, float w) {
        this.putFloat(x);
        this.putFloat(y);
        this.putFloat(z);
        this.putFloat(w);
        return this;
    }

    default AttribConsumer putVector4f(Matrix4fc matrix, float x, float y, float z, float w) {
        final Vector4f temp = TemporaryValues.VECTOR4F;
        temp.set(x, y, z, w);
        matrix.transform(temp);
        this.putFloat(temp.x);
        this.putFloat(temp.y);
        this.putFloat(temp.z);
        this.putFloat(temp.w);
        return this;
    }

    default AttribConsumer putVector4f(Vector4f vector) {
        return this.putVector4f(vector.x, vector.y, vector.z, vector.w);
    }

    default AttribConsumer putVector4f(Matrix4fc matrix, Vector4f vector) {
        return this.putVector4f(matrix, vector.x, vector.y, vector.z, vector.w);
    }

    default AttribConsumer putVector4f(Vector4fc vector) {
        return this.putVector4f(vector.x(), vector.y(), vector.z(), vector.w());
    }

    default AttribConsumer putVector4f(Matrix4fc matrix, Vector4fc vector) {
        return this.putVector4f(matrix, vector.x(), vector.y(), vector.z(), vector.w());
    }

    default AttribConsumer putVector4i(int x, int y, int z, int w) {
        this.putInt(x);
        this.putInt(y);
        this.putInt(z);
        this.putInt(w);
        return this;
    }

    default AttribConsumer putVector4i(Vector4i vector) {
        return this.putVector4i(vector.x, vector.y, vector.z, vector.w);
    }

    default AttribConsumer putVector4i(Vector4ic vector) {
        return this.putVector4i(vector.x(), vector.y(), vector.z(), vector.w());
    }

    default AttribConsumer putVector4d(double x, double y, double z, double w) {
        this.putDouble(x);
        this.putDouble(y);
        this.putDouble(z);
        this.putDouble(w);
        return this;
    }

    default AttribConsumer putVector4d(Matrix4dc matrix, double x, double y, double z, double w) {
        final Vector4d temp = TemporaryValues.VECTOR4D;
        temp.set(x, y, z, w);
        matrix.transform(temp);
        this.putDouble(temp.x);
        this.putDouble(temp.y);
        this.putDouble(temp.z);
        this.putDouble(temp.w);
        return this;
    }

    default AttribConsumer putVector4d(Vector4d vector) {
        return this.putVector4d(vector.x, vector.y, vector.z, vector.w);
    }

    default AttribConsumer putVector4d(Matrix4dc matrix, Vector4d vector) {
        return this.putVector4d(matrix, vector.x, vector.y, vector.z, vector.w);
    }

    default AttribConsumer putVector4d(Vector4dc vector) {
        return this.putVector4d(vector.x(), vector.y(), vector.z(), vector.w());
    }

    default AttribConsumer putVector4d(Matrix4dc matrix, Vector4dc vector) {
        return this.putVector4d(matrix, vector.x(), vector.y(), vector.z(), vector.w());
    }

    /**
     * Advances to the next attribute.
     */
    void next();

    /**
     * Advances to the next connected primitive. E.g. splits a line strip into multiple lines.
     */
    void nextConnectedPrimitive();
}
