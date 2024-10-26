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

package de.nekosarekawaii.vandalism.injection.mixins.integration;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.injection.access.IGameRenderer;
import de.nekosarekawaii.vandalism.integration.rotation.PrioritizedRotation;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class, priority = -1)
public abstract class MixinEntity implements MinecraftWrapper {

    @Inject(method = "getRotationVector(FF)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), cancellable = true)
    private void injectGetRotationVector(float pitch, float yaw, CallbackInfoReturnable<Vec3d> cir) {
        if (mc.player == (Object) this && !((IGameRenderer) mc.gameRenderer).vandalism$isSelfInflicted()) {
            final PrioritizedRotation rotation = Vandalism.getInstance().getRotationManager().getClientRotation();

            if (rotation != null) cir.setReturnValue(rotation.getVec());
        }
    }

}