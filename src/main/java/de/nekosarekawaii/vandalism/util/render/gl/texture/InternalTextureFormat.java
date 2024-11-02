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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL45C;

@Getter
@RequiredArgsConstructor
public enum InternalTextureFormat {

    DEPTH32F_STENCIL8(GL45C.GL_DEPTH32F_STENCIL8, TextureFormat.DEPTH_STENCIL),
    DEPTH24_STENCIL8(GL45C.GL_DEPTH24_STENCIL8, TextureFormat.DEPTH_STENCIL),
    DEPTH_COMPONENT16(GL45C.GL_DEPTH_COMPONENT16, TextureFormat.DEPTH_COMPONENT),
    DEPTH_COMPONENT24(GL45C.GL_DEPTH_COMPONENT24, TextureFormat.DEPTH_COMPONENT),
    DEPTH_COMPONENT32(GL45C.GL_DEPTH_COMPONENT32, TextureFormat.DEPTH_COMPONENT),
    DEPTH_COMPONENT32F(GL45C.GL_DEPTH_COMPONENT32F, TextureFormat.DEPTH_COMPONENT),
    DEPTH_COMPONENT(GL45C.GL_DEPTH_COMPONENT, TextureFormat.DEPTH_COMPONENT),
    DEPTH_STENCIL(GL45C.GL_DEPTH_STENCIL, TextureFormat.DEPTH_STENCIL),
    RED(GL45C.GL_RED, TextureFormat.RED),
    RG(GL45C.GL_RG, TextureFormat.RG),
    RGB(GL45C.GL_RGB, TextureFormat.RGB),
    RGBA(GL45C.GL_RGBA, TextureFormat.RGBA),
    R8(GL45C.GL_R8, TextureFormat.RED),
    R8_SNORM(GL45C.GL_R8_SNORM, TextureFormat.RED),
    R16(GL45C.GL_R16, TextureFormat.RED),
    R16_SNORM(GL45C.GL_R16_SNORM, TextureFormat.RED),
    RG8(GL45C.GL_RG8, TextureFormat.RG),
    RG8_SNORM(GL45C.GL_RG8_SNORM, TextureFormat.RG),
    RG16(GL45C.GL_RG16, TextureFormat.RG),
    RG16_SNORM(GL45C.GL_RG16_SNORM, TextureFormat.RG),
    R3_G3_B2(GL45C.GL_R3_G3_B2, TextureFormat.RGB),
    RGB4(GL45C.GL_RGB4, TextureFormat.RGB),
    RGB5(GL45C.GL_RGB5, TextureFormat.RGB),
    RGB8(GL45C.GL_RGB8, TextureFormat.RGB),
    RGB8_SNORM(GL45C.GL_RGB8_SNORM, TextureFormat.RGB),
    RGB10(GL45C.GL_RGB10, TextureFormat.RGB),
    RGB12(GL45C.GL_RGB12, TextureFormat.RGB),
    RGB16_SNORM(GL45C.GL_RGB16_SNORM, TextureFormat.RGB),
    RGBA2(GL45C.GL_RGBA2, TextureFormat.RGBA),
    RGBA4(GL45C.GL_RGBA4, TextureFormat.RGBA),
    RGB5_A1(GL45C.GL_RGB5_A1, TextureFormat.RGBA),
    RGBA8(GL45C.GL_RGBA8, TextureFormat.RGBA),
    RGBA8_SNORM(GL45C.GL_RGBA8_SNORM, TextureFormat.RGBA),
    RGB10_A2(GL45C.GL_RGB10_A2, TextureFormat.RGBA),
    RGB10_A2UI(GL45C.GL_RGB10_A2UI, TextureFormat.RGBA),
    RGBA12(GL45C.GL_RGBA12, TextureFormat.RGBA),
    RGBA16(GL45C.GL_RGBA16, TextureFormat.RGBA),
    SRGB8(GL45C.GL_SRGB8, TextureFormat.RGB),
    SRGB8_ALPHA8(GL45C.GL_SRGB8_ALPHA8, TextureFormat.RGBA),
    R16F(GL45C.GL_R16F, TextureFormat.RED),
    RG16F(GL45C.GL_RG16F, TextureFormat.RG),
    RGB16F(GL45C.GL_RGB16F, TextureFormat.RGB),
    RGBA16F(GL45C.GL_RGBA16F, TextureFormat.RGBA),
    R32F(GL45C.GL_R32F, TextureFormat.RED),
    RG32F(GL45C.GL_RG32F, TextureFormat.RG),
    RGB32F(GL45C.GL_RGB32F, TextureFormat.RGB),
    RGBA32F(GL45C.GL_RGBA32F, TextureFormat.RGBA),
    R11F_G11F_B10F(GL45C.GL_R11F_G11F_B10F, TextureFormat.RGB),
    RGB9_E5(GL45C.GL_RGB9_E5, TextureFormat.RGB),
    R8I(GL45C.GL_R8I, TextureFormat.RED),
    R8UI(GL45C.GL_R8UI, TextureFormat.RED),
    R16I(GL45C.GL_R16I, TextureFormat.RED),
    R16UI(GL45C.GL_R16UI, TextureFormat.RED),
    R32I(GL45C.GL_R32I, TextureFormat.RED),
    R32UI(GL45C.GL_R32UI, TextureFormat.RED),
    RG8I(GL45C.GL_RG8I, TextureFormat.RG),
    RG8UI(GL45C.GL_RG8UI, TextureFormat.RG),
    RG16I(GL45C.GL_RG16I, TextureFormat.RG),
    RG16UI(GL45C.GL_RG16UI, TextureFormat.RG),
    RG32I(GL45C.GL_RG32I, TextureFormat.RG),
    RG32UI(GL45C.GL_RG32UI, TextureFormat.RG),
    RGB8I(GL45C.GL_RGB8I, TextureFormat.RGB),
    RGB8UI(GL45C.GL_RGB8UI, TextureFormat.RGB),
    RGB16I(GL45C.GL_RGB16I, TextureFormat.RGB),
    RGB16UI(GL45C.GL_RGB16UI, TextureFormat.RGB),
    RGB32I(GL45C.GL_RGB32I, TextureFormat.RGB),
    RGB32UI(GL45C.GL_RGB32UI, TextureFormat.RGB),
    RGBA8I(GL45C.GL_RGBA8I, TextureFormat.RGBA),
    RGBA8UI(GL45C.GL_RGBA8UI, TextureFormat.RGBA),
    RGBA16I(GL45C.GL_RGBA16I, TextureFormat.RGBA),
    RGBA16UI(GL45C.GL_RGBA16UI, TextureFormat.RGBA),
    RGBA32I(GL45C.GL_RGBA32I, TextureFormat.RGBA),
    RGBA32UI(GL45C.GL_RGBA32UI, TextureFormat.RGBA);

    private final int glType;
    private final TextureFormat baseFormat;
}
