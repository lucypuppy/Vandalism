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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.velocity.BlocksmcModuleMode;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.modes.velocity.CancelModuleMode;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleModeValue;

public class VelocityModule extends AbstractModule {

    private final ModuleModeValue mode = new ModuleModeValue(this, "Mode", "The mode of the velocity.",
            new CancelModuleMode(this),
            new BlocksmcModuleMode(this)
    );

    public VelocityModule() {
        super("Velocity", "Modifies the server and the damage source velocity you take.", Category.MOVEMENT);
    }

}
