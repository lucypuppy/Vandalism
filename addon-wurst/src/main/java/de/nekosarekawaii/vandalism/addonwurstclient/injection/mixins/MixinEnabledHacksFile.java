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

package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins;

import de.nekosarekawaii.vandalism.addonwurstclient.AddonWurstClient;
import net.wurstclient.hack.EnabledHacksFile;
import net.wurstclient.hack.Hack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;
import java.util.stream.Stream;

@Mixin(value = EnabledHacksFile.class)
public abstract class MixinEnabledHacksFile {

    @Redirect(method = "createJson", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", ordinal = 0), remap = false)
    public <T extends Hack> Stream<T> useStoredEnabledData(Stream<T> instance, Predicate<? super T> predicate) {
        if (AddonWurstClient.enabledHacks != null) {
            // If the client is disabled and stored enabled hacks, we save
            // all enabled hacks we previously stored instead of checking the current state
            return instance.filter(module -> AddonWurstClient.enabledHacks.contains(module.getName()));
        } else {
            // Otherwise, do the normal stuff
            return instance.filter(predicate);
        }
    }

}
