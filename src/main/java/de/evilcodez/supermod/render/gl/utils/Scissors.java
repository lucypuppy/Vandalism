package de.evilcodez.supermod.render.gl.utils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.MinecraftClient;
import org.joml.Vector4i;
import org.lwjgl.opengl.GL45C;

public class Scissors {

    private final ObjectArrayList<Vector4i> scissors = new ObjectArrayList<>();

    public void push(int left, int top, int right, int bottom) {
        final Vector4i rect = new Vector4i(left, top, right, bottom);
        if (!scissors.isEmpty()) {
            this.intersection(scissors.top(), rect, rect);
        }
        this.scissors.push(rect);
        setScissorsScaled(rect.x, rect.y, rect.z, rect.w);
        enableScissors();
    }

    public void pop() {
        final boolean empty = this.scissors.isEmpty();
        if (!empty) {
            this.scissors.pop();
            if (scissors.isEmpty()) {
                disableScissors();
            } else {
                final Vector4i rect = scissors.top();
                enableScissors();
                setScissorsScaled(rect.x, rect.y, rect.z, rect.w);
            }
        }
    }

    private void intersection(Vector4i a, Vector4i b, Vector4i dest) {
        final int left = Math.max(a.x, b.x);
        final int top = Math.min(a.y, b.y);
        final int right = Math.min(a.z, b.z);
        final int bottom = Math.max(a.w, b.w);
        dest.set(left, top, right, bottom);
    }

    public static void enableScissors() {
        GL45C.glEnable(GL45C.GL_SCISSOR_TEST);
    }

    public static void disableScissors() {
        GL45C.glDisable(GL45C.GL_SCISSOR_TEST);
    }

    public static boolean isScissorsEnabled() {
        return GL45C.glIsEnabled(GL45C.GL_SCISSOR_TEST);
    }

    public static void setScissors(int left, int top, int right, int bottom) {
        GL45C.glScissor(left, bottom, right - left, top - bottom);
    }

    public static void setScissorsScaled(int left, int top, int right, int bottom) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        final double scale = mc.getWindow().getScaleFactor();
        final int height = mc.getWindow().getHeight();
        GL45C.glScissor((int) (left * scale), height - (int) (bottom * scale), (int) ((right - left) * scale), (int) ((bottom - top) * scale));
    }
}
