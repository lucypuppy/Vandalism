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

package de.nekosarekawaii.vandalism.injection.mixins.event;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.player.HealthUpdateListener;
import de.nekosarekawaii.vandalism.event.player.MoveFlyingListener;
import de.nekosarekawaii.vandalism.event.player.StrafeListener;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements MinecraftWrapper {

    @Unique
    private StrafeListener.StrafeEvent event;

    @ModifyArgs(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;<init>(DDD)V"))
    private void callMoveFlyingListener(final Args args) {
        if (this.mc.player == (Object) this) {
            final MoveFlyingListener.MoveFlyingEvent event = new MoveFlyingListener.MoveFlyingEvent(args.get(0), args.get(1), args.get(2));
            Vandalism.getInstance().getEventSystem().callExceptionally(MoveFlyingListener.MoveFlyingEvent.ID, event);

            args.set(0, event.sidewaysSpeed);
            args.set(1, event.upwardSpeed);
            args.set(2, event.forwardSpeed);
        }
    }

    @Inject(method = "jump", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V", shift = At.Shift.BEFORE))
    private void callStrafeListenerJump(CallbackInfo ci, @Local LocalFloatRef f) {
        if (mc.player == (Object) this) {
            event = new StrafeListener.StrafeEvent(null, -1, f.get(), StrafeListener.Type.JUMP);
            Vandalism.getInstance().getEventSystem().callExceptionally(StrafeListener.StrafeEvent.ID, event);
            f.set(event.yaw);
        }
    }

    @ModifyExpressionValue(method = "jump", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"))
    private float modifyJumpYaw(float original) {
        if (mc.player == (Object) this) {
            return event.modified ? (float) Math.toDegrees(event.yaw) : original;
        }
        return original;
    }

    @ModifyArgs(method = "setHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/data/DataTracker;set(Lnet/minecraft/entity/data/TrackedData;Ljava/lang/Object;)V"))
    private void callHealthUpdateListener(final Args args) {
        if (mc.player == (Object) this) {
            final HealthUpdateListener.HealthUpdateEvent event = new HealthUpdateListener.HealthUpdateEvent(args.get(1));
            Vandalism.getInstance().getEventSystem().callExceptionally(HealthUpdateListener.HealthUpdateEvent.ID, event);
            args.set(1, event.health);
        }
    }

}
