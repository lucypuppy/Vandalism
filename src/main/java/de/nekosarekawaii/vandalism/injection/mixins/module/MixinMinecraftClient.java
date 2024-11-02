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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.FastPlaceModule;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.NoChatReportsModule;
import de.nekosarekawaii.vandalism.feature.module.impl.render.ESPModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    public ClientPlayerEntity player;

    @Inject(method = "hasOutline", at = @At("RETURN"), cancellable = true)
    private void hookESP(final Entity entity, final CallbackInfoReturnable<Boolean> cir) {
        final ESPModule espModule = Vandalism.getInstance().getModuleManager().getEspModule();
        if (entity != this.player && espModule.isActive() && espModule.isTarget(entity)) {
            cir.setReturnValue(true);
        }
    }

    @ModifyConstant(method = "doItemUse", constant = @Constant(intValue = 4))
    private int hookFastPlace(final int value) {
        final FastPlaceModule fastPlaceModule = Vandalism.getInstance().getModuleManager().getFastPlaceModule();
        if (fastPlaceModule.isActive()) return fastPlaceModule.cooldown.getValue();
        return value;
    }

    @Inject(method = "getProfileKeys()Lnet/minecraft/client/session/ProfileKeys;", at = @At("HEAD"), cancellable = true)
    private void hookNoChatReports(final CallbackInfoReturnable<ProfileKeys> cir) {
        final NoChatReportsModule noChatReportsModule = Vandalism.getInstance().getModuleManager().getNoChatReportsModule();
        if (noChatReportsModule.isActive()) {
            cir.setReturnValue(ProfileKeys.MISSING);
        }
    }

}