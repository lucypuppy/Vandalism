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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.jesus;

import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.jesus.impl.StaticModuleMode;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.jesus.impl.VulcanModuleMode;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleModeValue;

public class JesusModule extends AbstractModule {

    private final ModuleModeValue<JesusModule> mode = new ModuleModeValue<>(
            this,
            "Mode",
            "The current jesus mode.",
            new StaticModuleMode(),
            new VulcanModuleMode()
    );

    public JesusModule() {
        super(
                "Jesus",
                "Allows you to walk on fluids.",
                Category.MOVEMENT
        );
    }

}
