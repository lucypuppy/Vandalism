/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.base.value.impl.misc;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.event.normal.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.util.render.InputType;
import imgui.ImGui;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class KeyBindValue extends Value<Integer> implements KeyboardInputListener {

    private final boolean onlyInGame;

    private boolean waitingForInput;

    public KeyBindValue(ValueParent parent, String name, String description) {
        this(parent, name, description, GLFW.GLFW_KEY_UNKNOWN);
    }

    public KeyBindValue(ValueParent parent, String name, String description, Integer defaultValue) {
        this(parent, name, description, defaultValue, true);
    }

    public KeyBindValue(ValueParent parent, String name, String description, Integer defaultValue, final boolean onlyInGame) {
        super(parent, name, description, defaultValue);
        this.onlyInGame = onlyInGame;
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

    @Override
    public void render() {
        final String id = "##" + this.getName() + this.getParent().getName();
        float width = 200;
        final float height = ImGui.getTextLineHeightWithSpacing();
        if (!this.waitingForInput) {
            if (ImGui.button(InputType.getKeyName(this.getValue()) + id, width, height)) {
                this.waitingForInput = true;
                Vandalism.getInstance().getEventSystem().subscribe(KeyboardInputEvent.ID, this);
            }
        } else {
            ImGui.textWrapped("Listening for key input...");
            width = 100;
            ImGui.sameLine();
            if (ImGui.button("Cancel" + id + "cancel", width, ImGui.getTextLineHeightWithSpacing())) {
                this.finishInput();
            }
            ImGui.sameLine();
            if (ImGui.button("Reset" + id + "reset", width, ImGui.getTextLineHeightWithSpacing())) {
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
                if (key == GLFW.GLFW_KEY_BACKSPACE) {
                    this.setValue(GLFW.GLFW_KEY_UNKNOWN);
                }
                else {
                    this.setValue(key);
                }
            }
        }
    }

    public boolean isPressed() {
        final MinecraftClient mc = MinecraftClient.getInstance();
        if (this.onlyInGame && (mc.player == null || mc.currentScreen != null)){
            return false;
        }
        return InputType.isPressed(this.getValue());
    }

    public boolean isPressed(final int keyCode) {
        if (keyCode == this.getValue()) {
            return this.isPressed();
        }
        return false;
    }

    public boolean isValid() {
        return this.getValue() != GLFW.GLFW_KEY_UNKNOWN;
    }

    /**
     * Don't use this method, use {@link #isPressed()} instead.
     *
     * @return the key code
     */
    @Override
    public Integer getValue() {
        return super.getValue();
    }

}
