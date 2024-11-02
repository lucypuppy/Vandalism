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

package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins.render;

import net.wurstclient.options.WurstOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WurstOptionsScreen.class)
public abstract class MixinWurstOptionsScreen {

    @Inject(method = "addSettingButtons", at = @At(value = "INVOKE", target = "Lnet/wurstclient/options/WurstOptionsScreen$WurstOptionsButton;<init>(Lnet/wurstclient/options/WurstOptionsScreen;IILjava/util/function/Supplier;Ljava/lang/String;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)V", ordinal = 1), cancellable = true)
    private void removeWurstCountUsersButton(final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "addManagerButtons", at = @At(value = "INVOKE", target = "Lnet/wurstclient/options/WurstOptionsScreen$WurstOptionsButton;<init>(Lnet/wurstclient/options/WurstOptionsScreen;IILjava/util/function/Supplier;Ljava/lang/String;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)V", ordinal = 2), cancellable = true)
    private void removeWurstZoomButton(final CallbackInfo ci) {
        ci.cancel();
    }

}
