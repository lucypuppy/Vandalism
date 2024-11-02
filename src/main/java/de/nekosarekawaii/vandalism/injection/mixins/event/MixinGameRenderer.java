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

package de.nekosarekawaii.vandalism.injection.mixins.event;

import com.llamalad7.mixinextras.sugar.Local;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.player.RaytraceListener;
import de.nekosarekawaii.vandalism.event.player.RotationListener;
import de.nekosarekawaii.vandalism.event.render.Render3DListener;
import de.nekosarekawaii.vandalism.injection.access.IGameRenderer;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer implements IGameRenderer, MinecraftWrapper {

    @Unique
    private static double vandalism$range = -1;

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V", shift = At.Shift.AFTER))
    private void callRotationListener(final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().callExceptionally(RotationListener.RotationEvent.ID, new RotationListener.RotationEvent());
    }

    @Redirect(method = "updateCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getEntityInteractionRange()D"))
    private double changeRange(ClientPlayerEntity instance) {
        if (vandalism$isSelfInflicted()) {
            return vandalism$range;
        } else {
            final RaytraceListener.RaytraceEvent event = new RaytraceListener.RaytraceEvent(instance.getEntityInteractionRange());
            Vandalism.getInstance().getEventSystem().callExceptionally(RaytraceListener.RaytraceEvent.ID, event);
            return event.range;
        }
    }

    @ModifyArg(method = "ensureTargetInRange", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;isInRange(Lnet/minecraft/util/math/Position;D)Z"))
    private static double changeMaxRange(final double constant) {
        if (vandalism$range != -1) {
            return Math.max(vandalism$range, constant);
        }
        return constant;
    }


    @Override
    public boolean vandalism$isSelfInflicted() {
        return vandalism$range != -1;
    }

    @Override
    public double vandalism$getRange() {
        return vandalism$range;
    }

    @Override
    public void vandalism$setRange(final double range) {
        vandalism$range = range;
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderHand(Lnet/minecraft/client/render/Camera;FLorg/joml/Matrix4f;)V"))
    private void callRender3DListener(RenderTickCounter tickCounter, CallbackInfo ci, @Local(ordinal = 1) Matrix4f matrix4f2) {
        MatrixStack matrices = new MatrixStack();
        matrices.multiplyPositionMatrix(matrix4f2);
        Vandalism.getInstance().getEventSystem().callExceptionally(
                Render3DListener.Render3DEvent.ID,
                new Render3DListener.Render3DEvent(tickCounter.getTickDelta(true), matrices)
        );
    }

}
