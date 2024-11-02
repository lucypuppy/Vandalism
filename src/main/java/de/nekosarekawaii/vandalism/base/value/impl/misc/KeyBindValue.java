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

package de.nekosarekawaii.vandalism.base.value.impl.misc;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.event.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.event.game.MouseInputListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.render.util.InputType;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class KeyBindValue extends Value<Integer> implements KeyboardInputListener, MouseInputListener {

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
            if (ImGui.button(InputType.getName(this.getValue()) + id, width, height)) {
                this.waitingForInput = true;
                Vandalism.getInstance().getEventSystem().subscribe(KeyboardInputEvent.ID, this);
                Vandalism.getInstance().getEventSystem().subscribe(MouseEvent.ID, this);
            }
            if (this.getDescription() != null && ImGui.isItemHovered()) {
                ImGui.beginTooltip();
                ImGui.text(this.getDescription());
                ImGui.endTooltip();
            }
            if (ImGui.isItemClicked(ImGuiMouseButton.Middle)) {
                this.resetValue();
            }
            if (this.getValue() != GLFW.GLFW_KEY_UNKNOWN) {
                final List<String> modules = new ArrayList<>();
                for (final Module module : Vandalism.getInstance().getModuleManager().getList()) {
                    final KeyBindValue moduleKeyBind = module.getKeyBind();
                    if (moduleKeyBind == this) {
                        continue;
                    }
                    if (this.getValue().intValue() == moduleKeyBind.getValue()) {
                        modules.add(module.getName());
                    }
                }
                if (!modules.isEmpty()) {
                    ImGui.sameLine();
                    ImGui.text("(Key is already bound)");
                    if (ImGui.isItemHovered()) {
                        ImGui.beginTooltip();
                        final List<String> alreadyBoundTo = new ArrayList<>();
                        alreadyBoundTo.add("Modules:");
                        alreadyBoundTo.addAll(modules);
                        for (final String string : alreadyBoundTo) {
                            ImGui.text(string);
                        }
                        ImGui.endTooltip();
                    }
                }
            }
        } else {
            ImGui.text("Listening for key input...");
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
        Vandalism.getInstance().getEventSystem().unsubscribe(MouseEvent.ID, this);
    }

    @Override
    public void onKeyInput(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (key != GLFW.GLFW_KEY_UNKNOWN && action == GLFW.GLFW_PRESS) {
            this.finishInput();
            if (key != GLFW.GLFW_KEY_ESCAPE) {
                if (key == GLFW.GLFW_KEY_BACKSPACE) {
                    this.setValue(GLFW.GLFW_KEY_UNKNOWN);
                } else {
                    this.setValue(key);
                }
            }
        }
    }

    @Override
    public void onMouse(final MouseEvent event) {
        if (event.type == MouseInputListener.Type.BUTTON) {
            if (event.button != GLFW.GLFW_KEY_UNKNOWN && event.action == GLFW.GLFW_PRESS) {
                this.finishInput();
                this.setValue(event.button);
            }
        }
    }

    public boolean isPressed() {
        final MinecraftClient mc = MinecraftClient.getInstance();
        if (this.onlyInGame && (mc.player == null || mc.currentScreen != null)) {
            return false;
        }
        return InputType.isPressed(this.getValue());
    }

    public boolean isReleased() {
        final MinecraftClient mc = MinecraftClient.getInstance();
        if (this.onlyInGame && (mc.player == null || mc.currentScreen != null)) {
            return false;
        }
        return InputType.isReleased(this.getValue());
    }

    public boolean isPressed(final int keyCode) {
        if (keyCode == this.getValue()) {
            return this.isPressed();
        }
        return false;
    }

    public boolean isReleased(final int keyCode) {
        if (keyCode == this.getValue()) {
            return this.isReleased();
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
