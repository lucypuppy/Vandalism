package de.vandalismdevelopment.vandalism.gui.imgui;

import de.vandalismdevelopment.vandalism.util.interfaces.MinecraftWrapper;
import imgui.ImGui;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.util.Window;

public class LWJGLImGuiRenderAdapter implements MinecraftWrapper {

    private Framebuffer framebuffer;
    private int width, height;

    public void begin(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.framebuffer = this.checkFramebuffer(this.framebuffer, width, height, true, true);
        this.framebuffer.beginWrite(false);
    }

    public void end() {
        this.mc().getFramebuffer().beginWrite(false);
        ImGui.image(this.framebuffer.getColorAttachment(), width, height);
    }

    private Framebuffer checkFramebuffer(final Framebuffer framebuffer, final boolean useDepth, final boolean getError) {
        final Window window = this.mc().getWindow();
        final int displayWidth = window.getWidth(), displayHeight = window.getHeight();
        return checkFramebuffer(framebuffer, displayWidth, displayHeight, useDepth, getError);
    }

    private Framebuffer checkFramebuffer(final Framebuffer framebuffer, final int width, final int height, final boolean useDepth, final boolean getError) {
        if (framebuffer == null) {
            return new SimpleFramebuffer(width, height, useDepth, getError);
        } else if (framebuffer.viewportWidth != width || framebuffer.viewportHeight != height) {
            framebuffer.resize(width, height, getError);
        }
        return framebuffer;
    }

}
