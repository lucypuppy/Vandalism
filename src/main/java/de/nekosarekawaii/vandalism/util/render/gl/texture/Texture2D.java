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

package de.nekosarekawaii.vandalism.util.render.gl.texture;

import de.nekosarekawaii.vandalism.util.render.gl.vertex.DataType;
import org.lwjgl.opengl.GL45C;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class Texture2D implements AutoCloseable {

    private final int id;

    private Texture2D() {
        this.id = GL45C.glCreateTextures(GL45C.GL_TEXTURE_2D);
    }

    private Texture2D(int id) {
        this.id = id;
    }

    public Texture2D(int levels, InternalTextureFormat internalFormat, int width, int height) {
        this();
        GL45C.glTextureStorage2D(this.id, levels, internalFormat.getGlType(), width, height);
    }

    public static Texture2D createMultisample(int samples, InternalTextureFormat internalFormat, int width, int height, boolean fixedSampleLocations) {
        final int id = GL45C.glCreateTextures(GL45C.GL_TEXTURE_2D_MULTISAMPLE);
        GL45C.glTextureStorage2DMultisample(id, samples, internalFormat.getGlType(), width, height, fixedSampleLocations);
        return new Texture2D(id);
    }

    public void upload(int level, int x, int y, int width, int height, TextureFormat format, DataType type, ByteBuffer data) {
        this.configureUnpacking(this.getAlignment(type));
        GL45C.glTextureSubImage2D(this.id, level, x, y, width, height, format.getGlType(), type.getGlType(), data);
        this.resetUnpacking();
    }

    public void upload(int level, int x, int y, int width, int height, TextureFormat format, DataType type, long data) {
        this.configureUnpacking(this.getAlignment(type));
        GL45C.glTextureSubImage2D(this.id, level, x, y, width, height, format.getGlType(), type.getGlType(), data);
        this.resetUnpacking();
    }

    public void upload(int level, int x, int y, int width, int height, TextureFormat format, DataType type, ShortBuffer data) {
        this.configureUnpacking(this.getAlignment(type));
        GL45C.glTextureSubImage2D(this.id, level, x, y, width, height, format.getGlType(), type.getGlType(), data);
        this.resetUnpacking();
    }

    public void upload(int level, int x, int y, int width, int height, TextureFormat format, DataType type, IntBuffer data) {
        this.configureUnpacking(this.getAlignment(type));
        GL45C.glTextureSubImage2D(this.id, level, x, y, width, height, format.getGlType(), type.getGlType(), data);
        this.resetUnpacking();
    }

    public void upload(int level, int x, int y, int width, int height, TextureFormat format, DataType type, FloatBuffer data) {
        this.configureUnpacking(this.getAlignment(type));
        GL45C.glTextureSubImage2D(this.id, level, x, y, width, height, format.getGlType(), type.getGlType(), data);
        this.resetUnpacking();
    }

    public void upload(int level, int x, int y, int width, int height, TextureFormat format, DataType type, DoubleBuffer data) {
        this.configureUnpacking(this.getAlignment(type));
        GL45C.glTextureSubImage2D(this.id, level, x, y, width, height, format.getGlType(), type.getGlType(), data);
        this.resetUnpacking();
    }

    public void upload(int level, int x, int y, int width, int height, TextureFormat format, DataType type, short[] data) {
        this.configureUnpacking(this.getAlignment(type));
        GL45C.glTextureSubImage2D(this.id, level, x, y, width, height, format.getGlType(), type.getGlType(), data);
        this.resetUnpacking();
    }

    public void upload(int level, int x, int y, int width, int height, TextureFormat format, DataType type, int[] data) {
        this.configureUnpacking(this.getAlignment(type));
        GL45C.glTextureSubImage2D(this.id, level, x, y, width, height, format.getGlType(), type.getGlType(), data);
        this.resetUnpacking();
    }

    public void upload(int level, int x, int y, int width, int height, TextureFormat format, DataType type, float[] data) {
        this.configureUnpacking(this.getAlignment(type));
        GL45C.glTextureSubImage2D(this.id, level, x, y, width, height, format.getGlType(), type.getGlType(), data);
        this.resetUnpacking();
    }

    public void upload(int level, int x, int y, int width, int height, TextureFormat format, DataType type, double[] data) {
        this.configureUnpacking(this.getAlignment(type));
        GL45C.glTextureSubImage2D(this.id, level, x, y, width, height, format.getGlType(), type.getGlType(), data);
        this.resetUnpacking();
    }

    public int getWidth(int level) {
        return GL45C.glGetTextureLevelParameteri(this.id, level, GL45C.GL_TEXTURE_WIDTH);
    }

    public int getWidth() {
        return this.getWidth(0);
    }

    public int getHeight(int level) {
        return GL45C.glGetTextureLevelParameteri(this.id, level, GL45C.GL_TEXTURE_HEIGHT);
    }

    public int getHeight() {
        return this.getHeight(0);
    }

    public void generateMipmaps() {
        GL45C.glGenerateTextureMipmap(this.id);
    }

    public void bindUnit(int unit) {
        GL45C.glBindTextureUnit(unit, this.id);
    }

    public void bind() {
        GL45C.glBindTexture(GL45C.GL_TEXTURE_2D, this.id);
    }

    public void unbind() {
        GL45C.glBindTexture(GL45C.GL_TEXTURE_2D, 0);
    }

    public void setMinFilter(int filter) {
        GL45C.glTextureParameteri(this.id, GL45C.GL_TEXTURE_MIN_FILTER, filter);
    }

    public void setMagFilter(int filter) {
        GL45C.glTextureParameteri(this.id, GL45C.GL_TEXTURE_MAG_FILTER, filter);
    }

    public void setFilter(int filter) {
        this.setMinFilter(filter);
        this.setMagFilter(filter);
    }

    public void setBaseLevel(int level) {
        GL45C.glTextureParameteri(this.id, GL45C.GL_TEXTURE_BASE_LEVEL, level);
    }

    public void setMinLOD(float lod) {
        GL45C.glTextureParameterf(this.id, GL45C.GL_TEXTURE_MIN_LOD, lod);
    }

    public void setMaxLOD(float lod) {
        GL45C.glTextureParameterf(this.id, GL45C.GL_TEXTURE_MAX_LOD, lod);
    }

    public void setLODBias(float bias) {
        GL45C.glTextureParameterf(this.id, GL45C.GL_TEXTURE_LOD_BIAS, bias);
    }

    public void setMaxLevel(int level) {
        GL45C.glTextureParameteri(this.id, GL45C.GL_TEXTURE_MAX_LEVEL, level);
    }

    public void setWrapS(int wrap) {
        GL45C.glTextureParameteri(this.id, GL45C.GL_TEXTURE_WRAP_S, wrap);
    }

    public void setWrapT(int wrap) {
        GL45C.glTextureParameteri(this.id, GL45C.GL_TEXTURE_WRAP_T, wrap);
    }

    public void setWrap(int wrap) {
        this.setWrapS(wrap);
        this.setWrapT(wrap);
    }

    public void setBorderColor(float r, float g, float b, float a) {
        GL45C.glTextureParameterfv(this.id, GL45C.GL_TEXTURE_BORDER_COLOR, new float[] { r, g, b, a });
    }

    public void clear(int level) {
        GL45C.glClearTexImage(this.id, level, GL45C.GL_RGBA, GL45C.GL_FLOAT, (ByteBuffer) null);
    }

    public int id() {
        return this.id;
    }

    private int prevUnpackingAlignment, prevUnpackingSkipPixels, prevUnpackingSkipRows, prevUnpackingRowLength;

    private int getAlignment(DataType type) {
        if (type.byteSize() % 8 == 0) return 8;
        if (type.byteSize() % 4 == 0) return 4;
        if (type.byteSize() % 2 == 0) return 2;
        return 1;
    }

    private void configureUnpacking(int alignment) {
        this.prevUnpackingAlignment = GL45C.glGetInteger(GL45C.GL_UNPACK_ALIGNMENT);
        this.prevUnpackingSkipPixels = GL45C.glGetInteger(GL45C.GL_UNPACK_SKIP_PIXELS);
        this.prevUnpackingSkipRows = GL45C.glGetInteger(GL45C.GL_UNPACK_SKIP_ROWS);
        this.prevUnpackingRowLength = GL45C.glGetInteger(GL45C.GL_UNPACK_ROW_LENGTH);
        GL45C.glPixelStorei(GL45C.GL_UNPACK_ALIGNMENT, 1);
        GL45C.glPixelStorei(GL45C.GL_UNPACK_SKIP_PIXELS, 0);
        GL45C.glPixelStorei(GL45C.GL_UNPACK_SKIP_ROWS, 0);
        GL45C.glPixelStorei(GL45C.GL_UNPACK_ROW_LENGTH, 0);
    }

    private void resetUnpacking() {
        GL45C.glPixelStorei(GL45C.GL_UNPACK_ALIGNMENT, this.prevUnpackingAlignment);
        GL45C.glPixelStorei(GL45C.GL_UNPACK_SKIP_PIXELS, this.prevUnpackingSkipPixels);
        GL45C.glPixelStorei(GL45C.GL_UNPACK_SKIP_ROWS, this.prevUnpackingSkipRows);
        GL45C.glPixelStorei(GL45C.GL_UNPACK_ROW_LENGTH, this.prevUnpackingRowLength);
    }

    @Override
    public void close() {
        GL45C.glDeleteTextures(this.id);
    }

    public static Texture2D byId(int id) {
        return new Texture2D(id);
    }

    public static int currentBoundId() {
        return GL45C.glGetInteger(GL45C.GL_TEXTURE_BINDING_2D);
    }

    public static Texture2D currentBound() {
        return byId(currentBoundId());
    }

    public static void bind(int id) {
        GL45C.glBindTexture(GL45C.GL_TEXTURE_2D, id);
    }
}
