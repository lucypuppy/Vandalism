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

package de.nekosarekawaii.vandalism.injection.mixins.integration;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.injection.access.ILivingEntity;
import de.nekosarekawaii.vandalism.integration.rotation.PrioritizedRotation;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements MinecraftWrapper, ILivingEntity {

    @Shadow
    public double serverX;

    @Shadow
    public double serverY;

    @Shadow
    public double serverZ;

    @Unique
    private Vec3d vandalism$prevServerPos;

    @Inject(method = "updateTrackedPositionAndAngles", at = @At("HEAD"))
    private void storePrevServerPos(final double x, final double y, final double z, final float yaw, final float pitch, final int interpolationSteps, final CallbackInfo ci) {
        this.vandalism$prevServerPos = new Vec3d(this.serverX, this.serverY, this.serverZ);
    }

    @Override
    public Vec3d vandalism$prevServerPos() {
        return this.vandalism$prevServerPos;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F", ordinal = 1)))
    private float modifyRotationYaw(final LivingEntity instance) {
        if (mc.player == (Object) this) {
            final PrioritizedRotation rotation = Vandalism.getInstance().getRotationManager().getClientRotation();
            if (rotation != null) return rotation.getYaw();
        }
        return instance.getYaw();
    }

    @Redirect(method = "turnHead", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"))
    private float modifyRotationHeadYaw(final LivingEntity instance) {
        if (mc.player == (Object) this) {
            final PrioritizedRotation rotation = Vandalism.getInstance().getRotationManager().getClientRotation();
            if (rotation != null) return rotation.getYaw();
        }
        return instance.getYaw();
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getPitch()F"))
    private float modifyRotationPitch(final LivingEntity instance) {
        if (mc.player == (Object) this) {
            final PrioritizedRotation rotation = Vandalism.getInstance().getRotationManager().getClientRotation();
            if (rotation != null) return rotation.getPitch();
        }
        return instance.getPitch();
    }

}
