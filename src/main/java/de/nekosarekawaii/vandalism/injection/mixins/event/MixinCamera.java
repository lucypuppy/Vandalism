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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.render.CameraClipRaytraceListener;
import de.nekosarekawaii.vandalism.event.render.CameraOverrideListener;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class MixinCamera {

    @Inject(method = "update", at = @At("TAIL"))
    public void callCameraOverrideListener(final net.minecraft.world.BlockView area, final Entity focusedEntity, final boolean thirdPerson, final boolean inverseView, final float tickDelta, final CallbackInfo ci) {
        final Camera camera = (Camera) (Object) this;
        Vandalism.getInstance().getEventSystem().callExceptionally(CameraOverrideListener.CameraOverrideEvent.ID, new CameraOverrideListener.CameraOverrideEvent(camera, tickDelta));
    }

    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    public void callCameraClipRaytraceListener(final float f, final CallbackInfoReturnable<Float> cir) {
        final CameraClipRaytraceListener.CameraClipRaytraceEvent event = new CameraClipRaytraceListener.CameraClipRaytraceEvent();
        Vandalism.getInstance().getEventSystem().callExceptionally(CameraClipRaytraceListener.CameraClipRaytraceEvent.ID, event);
        if (event.isCancelled()) {
            cir.setReturnValue(f);
        }
    }

}
