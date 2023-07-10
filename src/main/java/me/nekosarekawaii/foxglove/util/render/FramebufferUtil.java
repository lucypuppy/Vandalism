package me.nekosarekawaii.foxglove.util.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.util.Window;

public class FramebufferUtil {

    public static Framebuffer checkFramebuffer(final Framebuffer framebuffer, final boolean useDepth, final boolean getError) {
        final Window window = MinecraftClient.getInstance().getWindow();
        final int displayWidth = window.getWidth(), displayHeight = window.getHeight();
        return checkFramebuffer(framebuffer, displayWidth, displayHeight, useDepth, getError);
    }

    public static Framebuffer checkFramebuffer(final Framebuffer framebuffer, final int width, final int height, final boolean useDepth, final boolean getError) {
        if (framebuffer == null) {
            return new SimpleFramebuffer(width, height, useDepth, getError);
        } else if (framebuffer.viewportWidth != width || framebuffer.viewportHeight != height) {
            framebuffer.resize(width, height, getError);
        }

        return framebuffer;
    }

}
