/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

import de.nekosarekawaii.vandalism.util.IName;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.StringUtils;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import imgui.ImGui;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;

public class ClientWindow implements IName, MinecraftWrapper {

    private final String name;

    @Getter
    private final Category category;

    protected final float width;
    protected final float height;
    protected final int windowFlags;

    @Getter
    private boolean active;

    public ClientWindow(final String name, final Category category, final float width, final float height) {
        this(name, category, width, height, -1);
    }

    public ClientWindow(final String name, final Category category, final float width, final float height, final int windowFlags) {
        this.name = name;
        this.category = category;
        this.width = width;
        this.height = height;
        this.windowFlags = windowFlags;
    }

    protected void init() {
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        ImGui.setNextWindowSizeConstraints(ImUtils.modulateDimension(this.width), ImUtils.modulateDimension(this.height), ImUtils.modulateDimension(1000000f), ImUtils.modulateDimension(1000000f));
        if (this.windowFlags != -1) {
            ImGui.begin(this.getName(), this.windowFlags);
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
        this.setActive(!this.isActive());
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
