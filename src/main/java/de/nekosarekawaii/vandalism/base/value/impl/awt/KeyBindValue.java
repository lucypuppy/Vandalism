/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.base.value.impl.awt;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.normal.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import de.nekosarekawaii.vandalism.util.render.InputType;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;

public class KeyBindValue extends Value<Integer> implements KeyboardInputListener {

    public KeyBindValue(ValueParent parent, String name, String description) {
        this(parent, name, description, GLFW.GLFW_KEY_UNKNOWN);
    }

    public KeyBindValue(ValueParent parent, String name, String description, Integer defaultValue) {
        super(parent, name, description, defaultValue);
    }

    @Override
    public void load(final JsonObject mainNode) {
        if (!mainNode.has(this.getName())) {
            return;
        }
        this.setValue(mainNode.get(this.getName()).getAsInt());
    }

    @Override
    public void save(final JsonObject mainNode) {
        mainNode.addProperty(this.getName(), getValue());
    }

    private boolean waitingForInput;

    @Override
    public void render() {
        final String id = "##" + this.getName() + this.getParent().getName();
        if (!this.waitingForInput) {
            if (ImUtils.subButton(InputType.getKeyName(this.getValue()) + id)) {
                this.waitingForInput = true;
                Vandalism.getInstance().getEventSystem().subscribe(KeyboardInputEvent.ID, this);
            }
        } else {
            ImGui.textWrapped("Listening for key input...");
            if (ImUtils.subButton("Cancel" + id + "cancel")) {
                this.finishInput();
            }
            ImGui.sameLine();
            if (ImUtils.subButton("Reset" + id + "reset")) {
                this.finishInput();
                this.resetValue();
            }
        }
    }

    private void finishInput() {
        this.waitingForInput = false;
        Vandalism.getInstance().getEventSystem().unsubscribe(KeyboardInputEvent.ID, this);
    }

    @Override
    public void onKeyInput(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (key != GLFW.GLFW_KEY_UNKNOWN && action == GLFW.GLFW_PRESS) {
            this.finishInput();
            if (key != GLFW.GLFW_KEY_ESCAPE) {
                this.setValue(key);
            }
        }
    }

    public boolean isPressed() {
        return InputType.isPressed(this.getValue());
    }

    public boolean isValid() {
        return this.getValue() != GLFW.GLFW_KEY_UNKNOWN;
    }

}
