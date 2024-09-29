/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.FastBreakModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.NoSlowModule;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity implements MinecraftWrapper {

    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    private void hookFastBreak(final CallbackInfoReturnable<Float> cir) {
        final FastBreakModule fastBreakModule = Vandalism.getInstance().getModuleManager().getFastBreakModule();
        if (fastBreakModule.isActive()) {
            cir.setReturnValue(cir.getReturnValue() * 1.0F + fastBreakModule.blockBreakingSpeed.getValue() * 0.2F);
        }
    }

    @WrapWithCondition(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"))
    private boolean hookNoSlow(final PlayerEntity entity, final Vec3d vec3d) {
        if (entity == mc.player) {
            final NoSlowModule noSlowModule = Vandalism.getInstance().getModuleManager().getNoSlowModule();
            if (noSlowModule.isActive() && noSlowModule.noHitSlowdown.getValue()) {
                return false;
            }
        }
        return true;
    }

    @WrapWithCondition(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V"))
    private boolean hookNoSlow(final PlayerEntity instance, final boolean b) {
        if (instance == mc.player) {
            final NoSlowModule noSlowModule = Vandalism.getInstance().getModuleManager().getNoSlowModule();
            if (noSlowModule.isActive() && noSlowModule.noHitSlowdown.getValue() && !b) {
                return false;
            }
        }
        return true;
    }

}
