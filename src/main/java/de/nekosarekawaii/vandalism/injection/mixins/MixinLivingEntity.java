/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

package de.nekosarekawaii.vandalism.injection.mixins;

import de.nekosarekawaii.vandalism.injection.access.ILivingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements ILivingEntity {

    @Shadow
    public double serverX;

    @Shadow
    public double serverY;

    @Shadow
    public double serverZ;

    @Unique
    private Vec3d vandalism$prevServerPos;

    @Inject(method = "updateTrackedPositionAndAngles", at = @At("HEAD"))
    private void storePrevServerPos(double x, double y, double z, float yaw, float pitch, int interpolationSteps, CallbackInfo ci) {
        vandalism$prevServerPos = new Vec3d(serverX, serverY, serverZ);
    }

    @Override
    public Vec3d vandalism$prevServerPos() {
        return vandalism$prevServerPos;
    }

}
