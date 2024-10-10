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

package de.nekosarekawaii.vandalism.feature.module.template.module;

import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.integration.clicker.ClickerModeValue;
import net.raphimc.vialoader.util.VersionRange;

public abstract class ClickerModule extends Module {

    public final ValueGroup clickerGroup = new ValueGroup(this, "Clicker", "The settings for the clicker.");

    public final ClickerModeValue mode = new ClickerModeValue(this.clickerGroup, "Mode", "The mode of the clicker.", this);

    public ClickerModule(final String name, final String description, final Category category) {
        this(name, description, category, null);
    }

    public ClickerModule(final String name, final String description, final Category category, final VersionRange supportedVersions) {
        super(name, description, category, supportedVersions);
    }

    @Override
    protected void onActivate() {
        this.mode.getValue().onActivate();
    }

    @Override
    protected void onDeactivate() {
        this.mode.getValue().onDeactivate();
    }

    public abstract void onClick();

    public abstract void onFailClick();

    public boolean shouldClick() {
        return true;
    }

}
