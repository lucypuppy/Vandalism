/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

package de.nekosarekawaii.vandalism.clientwindow.base;

import de.florianmichael.rclasses.common.StringUtils;
import de.florianmichael.rclasses.pattern.functional.IName;
import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import imgui.ImGui;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;

public class ClientWindow implements IName, MinecraftWrapper {

    private final String name;

    @Getter
    private final Category category;

    private final int defaultWindowFlags;

    @Getter
    private boolean active;

    public ClientWindow(final String name, final Category category) {
        this(name, category, -1);
    }

    public ClientWindow(final String name, final Category category, final int defaultWindowFlags) {
        this.name = name;
        this.category = category;
        this.defaultWindowFlags = defaultWindowFlags;
    }

    protected void init() {
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        if (this.defaultWindowFlags != -1) {
            ImGui.begin(this.getName(), this.defaultWindowFlags);
        } else {
            ImGui.begin(this.getName());
        }
        this.onRender(context, mouseX, mouseY, delta);
        ImGui.end();
    }

    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
    }

    public void mouseClicked(final double mouseX, final double mouseY, final int button, final boolean release) {
    }

    public boolean keyPressed(final int key, final int scanCode, final int modifiers, final boolean release) {
        return true;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

    public void toggle() {
        setActive(!isActive());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Getter
    public enum Category {

        CONFIG,
        SERVER,
        MISC;

        private final String name;

        Category() {
            this.name = StringUtils.normalizeEnumName(this.name());
        }

    }

}
