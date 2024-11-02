/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.util.render.util;

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