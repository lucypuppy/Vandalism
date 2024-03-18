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

package de.nekosarekawaii.vandalism.clientmenu.base;

import de.florianmichael.rclasses.common.StringUtils;
import de.florianmichael.rclasses.pattern.functional.IName;
import de.nekosarekawaii.vandalism.util.wrapper.MinecraftWrapper;
import net.minecraft.client.gui.DrawContext;

public class ClientMenuWindow implements IName, MinecraftWrapper {

    private final String name;
    private final Category category;

    private boolean active;

    public ClientMenuWindow(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    protected void onEnable() {
    }
    protected void onDisable() {
    }

    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
    }
    public void mouseClicked(final double mouseX, final double mouseY, final int button, final boolean release) {
    }

    public boolean keyPressed(final int key, final int scanCode, final int modifiers, final boolean release) {
        return true;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;

        if (active) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void toggle() {
        setActive(!isActive());
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Category getCategory() {
        return this.category;
    }

    public enum Category {

        CONFIG,
        SERVER,
        MISC;

        private final String name;

        Category() {
            this.name = StringUtils.normalizeEnumName(this.name());
        }

        public String getName() {
            return this.name;
        }

    }

}
