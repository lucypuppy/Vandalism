package me.nekosarekawaii.foxglove.gui.imgui;

import imgui.ImGui;
import me.nekosarekawaii.foxglove.util.render.FramebufferUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;

public class LWJGLImGuiRenderAdapter {

    private Framebuffer framebuffer;
    private int width, height;

    public void begin(final int width, final int height) {
        this.width = width;
        this.height = height;

        this.framebuffer = FramebufferUtil.checkFramebuffer(this.framebuffer, width, height, true, true);
        this.framebuffer.beginWrite(false);
    }

    public void end() {
        MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        ImGui.image(this.framebuffer.getColorAttachment(), width, height);
    }

}
