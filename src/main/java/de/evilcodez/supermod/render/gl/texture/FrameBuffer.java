package de.evilcodez.supermod.render.gl.texture;

import org.lwjgl.opengl.GL45C;

public class FrameBuffer implements AutoCloseable {

    private final int id;

    private FrameBuffer(int id) {
        this.id = id;
    }

    public FrameBuffer() {
        this.id = GL45C.glCreateFramebuffers();
    }

    public int id() {
        return this.id;
    }

    public int getCompleteStatus() {
        return GL45C.glCheckNamedFramebufferStatus(this.id, GL45C.GL_FRAMEBUFFER);
    }

    public boolean isComplete() {
        return this.getCompleteStatus() == GL45C.GL_FRAMEBUFFER_COMPLETE;
    }

    public void bind() {
        GL45C.glBindFramebuffer(GL45C.GL_FRAMEBUFFER, this.id);
    }

    public void clearColor(float r, float g, float b, float a) {
        GL45C.glClearNamedFramebufferfv(this.id, GL45C.GL_COLOR, 0, new float[] {r, g, b, a});
    }

    public void clearColor() {
        this.clearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void clearDepthStencil() {
        GL45C.glClearNamedFramebufferfi(this.id, GL45C.GL_DEPTH_STENCIL, 0, 1.0f, 0);
    }

    public void clear() {
        this.clearColor();
        this.clearDepthStencil();
    }

    public void attachTexture(AttachmentType type, int texture, int level) {
        GL45C.glNamedFramebufferTexture(this.id, type.getGlType(), texture, level);
    }

    public void attachTexture(AttachmentType type, Texture2D texture, int level) {
        this.attachTexture(type, texture.id(), level);
    }

    public void attachTexture(AttachmentType type, int texture) {
        this.attachTexture(type, texture, 0);
    }

    public void attachTexture(AttachmentType type, Texture2D texture) {
        this.attachTexture(type, texture, 0);
    }

    public int getAttachment(AttachmentType type) {
        return GL45C.glGetNamedFramebufferAttachmentParameteri(this.id, type.getGlType(), GL45C.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME);
    }

    public int getAttachmentType(AttachmentType type) {
        return GL45C.glGetNamedFramebufferAttachmentParameteri(this.id, type.getGlType(), GL45C.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE);
    }

    @Override
    public void close() {
        GL45C.glDeleteFramebuffers(this.id);
    }

    public static FrameBuffer byId(int id) {
        return new FrameBuffer(id);
    }

    public static void bind(int id) {
        GL45C.glBindFramebuffer(GL45C.GL_FRAMEBUFFER, id);
    }

    public static void unbind() {
        GL45C.glBindFramebuffer(GL45C.GL_FRAMEBUFFER, 0);
    }

    public static int currentBoundId() {
        return GL45C.glGetInteger(GL45C.GL_FRAMEBUFFER_BINDING);
    }

    public static FrameBuffer currentBound() {
        return byId(currentBoundId());
    }
}
