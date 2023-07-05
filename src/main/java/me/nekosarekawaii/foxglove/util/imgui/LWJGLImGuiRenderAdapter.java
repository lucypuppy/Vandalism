package me.nekosarekawaii.foxglove.util.imgui;

import me.nekosarekawaii.foxglove.util.FramebufferUtil;
import net.minecraft.client.gl.Framebuffer;

public class LWJGLImGuiRenderAdapter {

    private Framebuffer framebuffer;

    public void begin() {
        this.framebuffer = FramebufferUtil.checkFramebuffer(this.framebuffer, true, true);
        this.framebuffer.beginWrite(false);
    }

    public void end(final float width, final float height) {
        this.framebuffer.endWrite();
    }

}
