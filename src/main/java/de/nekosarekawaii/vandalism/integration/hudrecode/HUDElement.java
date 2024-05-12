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

package de.nekosarekawaii.vandalism.integration.hudrecode;

import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.util.common.IName;
import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class HUDElement implements IName, ValueParent, MinecraftWrapper {

    private final String name;
    private final List<Value<?>> values;

    @Getter
    private final BooleanValue active;
    private final Position position;

    public HUDElement(final String name) {
        this(name, true, new Position());
    }

    public HUDElement(final String name, final boolean defaultActive, final Position position) {
        this.name = name;
        this.position = position;
        this.values = new ArrayList<>();
        this.active = new BooleanValue(
                this,
                "Active",
                "Whether this HUD element is active.",
                defaultActive
        );
    }

    public void reset() {
        this.position.reset();
        for (final Value<?> value : this.getValues()) {
            value.resetValue();
        }
    }

    protected abstract void onRender(final DrawContext context, final float delta);

    public void onKeyInput(final long window, final int key, final int scanCode, final int action, final int modifiers) {}

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

    @Override
    public String getName() {
        return this.name;
    }

}