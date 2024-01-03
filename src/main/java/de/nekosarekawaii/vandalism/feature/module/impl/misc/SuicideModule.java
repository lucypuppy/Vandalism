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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.modes.suicide.BoatModuleMode;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleModeValue;

public class SuicideModule extends AbstractModule {

    public final ModuleModeValue<SuicideModule> mode = new ModuleModeValue<>(
            this,
            "Mode",
            "The current suicide mode.",
            new BoatModuleMode(this)
    );

    public SuicideModule() {
        super(
                "Suicide",
                "Allows you to kill your self.",
                Category.MISC
        );
        this.deactivateAfterSession();
    }

}
