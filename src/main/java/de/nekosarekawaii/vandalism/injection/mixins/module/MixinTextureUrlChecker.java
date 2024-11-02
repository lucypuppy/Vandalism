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

package de.nekosarekawaii.vandalism.injection.mixins.module;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.authlib.yggdrasil.TextureUrlChecker;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.exploitfixer.ExploitFixerModule;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import de.nekosarekawaii.vandalism.util.MinecraftConstants;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TextureUrlChecker.class, remap = false)
public abstract class MixinTextureUrlChecker {

    @Inject(method = "isAllowedTextureDomain", at = @At("HEAD"), cancellable = true)
    private static void hookExploitFixer(final String url, final CallbackInfoReturnable<Boolean> cir, @Share("state") LocalBooleanRef state) {
        final ExploitFixerModule exploitFixerModule = Vandalism.getInstance().getModuleManager().getExploitFixerModule();
        state.set(exploitFixerModule.isActive() && exploitFixerModule.miscSettings.blockInvalidTextureUrls.getValue());
        if (state.get() && url == null) {
            // URL can be set to null which crashes vanilla clients
            ChatUtil.warningChatMessage(Text.literal("Blocked null texture url"), Vandalism.getInstance().getModuleManager().getExploitFixerModule().sameLineWarnings.getValue());
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isAllowedTextureDomain", at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/yggdrasil/TextureUrlChecker;isDomainOnList(Ljava/lang/String;Ljava/util/List;)Z", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    private static void hookExploitFixer(String url, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 3) String lowerCaseDomain, @Share("state") LocalBooleanRef state) {
        if (state.get() && !lowerCaseDomain.equals(MinecraftConstants.TEXTURE_ENDPOINT)) {
            // Validate against the only possible texture endpoint and drop everything else
            ChatUtil.warningChatMessage(Text.literal("Blocked invalid texture endpoint: " + url), Vandalism.getInstance().getModuleManager().getExploitFixerModule().sameLineWarnings.getValue());
            cir.setReturnValue(false);
        }
    }

}
