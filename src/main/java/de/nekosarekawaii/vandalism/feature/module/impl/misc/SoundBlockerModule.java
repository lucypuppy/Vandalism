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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import com.google.common.collect.Lists;
import de.nekosarekawaii.vandalism.base.value.impl.minecraft.MultiRegistryBlacklistValue;
import de.nekosarekawaii.vandalism.feature.module.Module;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;

public class SoundBlockerModule extends Module {

    public final MultiRegistryBlacklistValue<SoundEvent> blockedSounds = new MultiRegistryBlacklistValue<>(
            this,
            "Blocked Sounds",
            "The sounds that should be blocked.",
            Registries.SOUND_EVENT,
            Lists.newArrayList()
    );

    public SoundBlockerModule() {
        super(
                "Sound Blocker",
                "Allows you to block certain sounds from being played.",
                Category.MISC
        );
    }

}
