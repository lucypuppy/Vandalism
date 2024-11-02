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

package de.nekosarekawaii.vandalism.clientwindow.template;

import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import net.minecraft.client.gui.DrawContext;

public class StateClientWindow extends ClientWindow {

    private final ImString state = new ImString(200);

    public StateClientWindow(final String name, final Category category, final float defaultWidth, final float defaultHeight) {
        super(name, category, defaultWidth, defaultHeight);
        this.resetState();
    }

    public StateClientWindow(final String name, final Category category, final float defaultWidth, final float defaultHeight, final int defaultWindowFlags) {
        super(name, category, defaultWidth, defaultHeight, defaultWindowFlags);
        this.resetState();
    }

    public String getState() {
        return this.state.get();
    }

    protected void setState(final String state) {
        this.state.set(state);
    }

    protected void resetState() {
        this.setState("Waiting for input...");
    }

    protected void delayedResetState(final long delay) {
        try {
            Thread.sleep(delay);
        } catch (final InterruptedException ignored) {
        }
        this.resetState();
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        ImGui.text("State");
        ImGui.setNextItemWidth(-1);
        ImGui.inputText("##" + this.getName() + "State", this.state, ImGuiInputTextFlags.ReadOnly);
        ImGui.spacing();
    }

}
