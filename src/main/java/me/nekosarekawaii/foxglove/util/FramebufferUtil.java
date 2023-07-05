package me.nekosarekawaii.foxglove.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.util.Window;

public class FramebufferUtil {

    public static Framebuffer checkFramebuffer(final Framebuffer framebuffer, final boolean useDepth, final boolean getError) {
        final Window window = MinecraftClient.getInstance().getWindow();

        final int displayWidth = window.getWidth(), displayHeight = window.getHeight();

        if (framebuffer == null) {
            return new SimpleFramebuffer(displayWidth, displayHeight, useDepth, getError);
        } else if (framebuffer.viewportWidth != displayWidth || framebuffer.viewportHeight != displayHeight) {
            framebuffer.resize(displayWidth, displayHeight, getError);
        }

        return framebuffer;
    }

}
