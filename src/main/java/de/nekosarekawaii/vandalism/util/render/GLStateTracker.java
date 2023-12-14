package de.nekosarekawaii.vandalism.util.render;

import org.lwjgl.opengl.GL11C;

/**
 * This enum is used to track the state of OpenGL states. This is used to save the current state of a OpenGL state,
 * change it and revert it to the saved state. This is used to prevent the state from being changed permanently.
 */
public enum GLStateTracker {
    CULL_FACE(GL11C.GL_CULL_FACE),
    BLEND(GL11C.GL_BLEND),
    DEPTH_TEST(GL11C.GL_DEPTH_TEST),
    LINE_SMOOTH(GL11C.GL_LINE_SMOOTH);

    private final int glCap;
    private boolean currentState;

    GLStateTracker(final int glCap) {
        this.glCap = glCap;
    }

    /**
     * @return current state of this.glCap
     */
    public boolean get() {
        return GL11C.glIsEnabled(this.glCap);
    }

    /**
     * Saves the current state of this.glCap and set it to @param newValue
     *
     * @param newValue new state of this.glCap
     */
    public void save(final boolean newValue) {
        this.currentState = this.get();
        this.set(newValue);
    }

    /**
     * Set the current state of this.glCap to @param newValue
     *
     * @param newValue new state of this.glCap
     */
    public void set(final boolean newValue) {
        if (newValue)
            GL11C.glEnable(this.glCap);
        else
            GL11C.glDisable(this.glCap);
    }

    /**
     * Revert the current state of this.glCap to this.currentState and updates the field
     */
    public void revert() {
        this.set(this.currentState);
        this.currentState = this.get();
    }

}