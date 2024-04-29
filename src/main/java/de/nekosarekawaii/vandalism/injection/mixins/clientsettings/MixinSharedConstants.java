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

package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SharedConstants.class)
public abstract class MixinSharedConstants {

    // TODO: Fix
    /*@Inject(method = "isValidChar", at = @At("HEAD"), cancellable = true)
    private static void allowColorChar(char chr, CallbackInfoReturnable<Boolean> cir) {
        if (Vandalism.getInstance().getClientSettings().getChatSettings().allowColorChar.getValue()) {
            if (chr == Formatting.FORMATTING_CODE_PREFIX) {
                cir.setReturnValue(true);
            }
        }
    }*/

}
