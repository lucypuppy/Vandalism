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

import org.lwjgl.opengl.GL45C;

public class Stencil {

    public static void enableTesting() {
        GL45C.glEnable(GL45C.GL_STENCIL_TEST);
    }

    public static void disableTesting() {
        GL45C.glDisable(GL45C.GL_STENCIL_TEST);
    }

    public static void setMask(int mask) {
        GL45C.glStencilMask(mask);
    }

    public static void enableWriting(int mask) {
        GL45C.glStencilMask(mask);
    }

    public static void enableWriting() {
        GL45C.glStencilMask(0xFF);
    }

    public static void disableWriting() {
        GL45C.glStencilMask(0x00);
    }

    public static void setStencilFunc(StencilFunc func, int ref, int mask) {
        GL45C.glStencilFunc(func.getGlType(), ref, mask);
    }

    public static void setStencilOp(StencilOp stencilFail, StencilOp depthFail, StencilOp depthPassed) {
        GL45C.glStencilOp(stencilFail.getGlType(), depthFail.getGlType(), depthPassed.getGlType());
    }

    public static void setColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        GL45C.glColorMask(red, green, blue, alpha);
    }

    public static void enableColorWriting() {
        GL45C.glColorMask(true, true, true, true);
    }

    public static void disableColorWriting() {
        GL45C.glColorMask(false, false, false, false);
    }
}
