package de.evilcodez.supermod.render.gl.utils;

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
