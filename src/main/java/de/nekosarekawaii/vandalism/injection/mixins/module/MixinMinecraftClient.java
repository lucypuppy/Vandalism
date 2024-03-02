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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.FastPlaceModule;
import de.nekosarekawaii.vandalism.util.wrapper.MinecraftWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MinecraftClient.class)
public abstract class MixinMinecraftClient implements MinecraftWrapper {

    @Inject(method = "hasOutline", at = @At("RETURN"), cancellable = true)
    private void hookEsp(final Entity entity, final CallbackInfoReturnable<Boolean> cir) {
        if (entity == this.mc.player) return;
        if (Vandalism.getInstance().getModuleManager().getEspModule().isActive()) {
            cir.setReturnValue(true);
        }
    }

    @ModifyConstant(method = "doItemUse", constant = @Constant(intValue = 4))
    private int hookFastUse(final int value) {
        final FastPlaceModule fastPlaceModule = Vandalism.getInstance().getModuleManager().getFastPlaceModule();
        if (fastPlaceModule.isActive()) return fastPlaceModule.cooldown.getValue();
        return value;
    }

}