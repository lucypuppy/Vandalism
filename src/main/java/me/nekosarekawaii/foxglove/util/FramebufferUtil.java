package me.nekosarekawaii.foxglove.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;

public class FramebufferUtil {

    public static Framebuffer checkFramebuffer(final Framebuffer framebuffer, final boolean useDepth, final boolean getError) {
        final int displayWidth = MinecraftClient.getInstance().getWindow().getWidth();
        final int displayHeight = MinecraftClient.getInstance().getWindow().getHeight();

        if (framebuffer == null) {
            return new SimpleFramebuffer(displayWidth, displayHeight, useDepth, getError);
        } else if (framebuffer.viewportWidth != displayWidth || framebuffer.viewportHeight != displayHeight) {
            framebuffer.resize(displayWidth, displayHeight, getError);
        }

        return framebuffer;
    }

}
