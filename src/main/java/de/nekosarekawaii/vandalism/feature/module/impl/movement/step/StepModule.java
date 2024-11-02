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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.step;

import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.step.impl.InstantModuleMode;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.step.impl.NCPModuleMode;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleModeValue;

public class StepModule extends Module {

    public final ModuleModeValue<StepModule> mode = new ModuleModeValue<>(this, "Mode", "The mode of the step.",
            new InstantModuleMode(),
            new NCPModuleMode()
    );

    public StepModule() {
        super("Step", "Changes the way you step up blocks.", Category.MOVEMENT);
    }

}
