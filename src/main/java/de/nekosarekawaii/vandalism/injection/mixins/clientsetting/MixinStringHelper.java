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

package de.nekosarekawaii.vandalism.injection.mixins.clientsetting;

import de.nekosarekawaii.vandalism.Vandalism;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StringHelper.class)
public abstract class MixinStringHelper {

    @Inject(method = "isValidChar", at = @At("HEAD"), cancellable = true)
    private static void allowColorChar(char chr, CallbackInfoReturnable<Boolean> cir) {
        if (Vandalism.getInstance().getClientSettings().getChatSettings().allowColorChar.getValue()) {
            if (chr == Formatting.FORMATTING_CODE_PREFIX) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "truncateChat", at = @At(value = "HEAD"), cancellable = true)
    private static void moreChatInput(final String text, final CallbackInfoReturnable<String> cir) {
        if (Vandalism.getInstance().getClientSettings().getChatSettings().moreChatInput.getValue()) {
            cir.setReturnValue(text);
        }
    }

}
