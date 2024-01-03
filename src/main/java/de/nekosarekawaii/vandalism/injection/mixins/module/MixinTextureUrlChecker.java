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

package de.nekosarekawaii.vandalism.injection.mixins.module;

import com.mojang.authlib.yggdrasil.TextureUrlChecker;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.ExploitFixerModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TextureUrlChecker.class, remap = false)
public abstract class MixinTextureUrlChecker {

    @Inject(method = "isAllowedTextureDomain", at = @At("HEAD"), cancellable = true)
    private static void hookExploitFixer(final String url, final CallbackInfoReturnable<Boolean> cir) {
        final var exploitFixerModule = Vandalism.getInstance().getModuleManager().getExploitFixerModule();

        if (exploitFixerModule.isActive() && exploitFixerModule.blockInvalidTextureUrls.getValue()) {
            if (!url.toLowerCase().startsWith("https://" + ExploitFixerModule.CORRECT_TEXTURE_URL_START) && !url.toLowerCase().startsWith("http://" + ExploitFixerModule.CORRECT_TEXTURE_URL_START)) {
                cir.setReturnValue(false);
            }
        }
    }

}
