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

package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.StateTypes;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.entity.MotionListener;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {

    @Inject(method = "tick", at = @At("HEAD"))
    private void callMotionListener_pre(final CallbackInfo ci) {
        final MotionListener.MotionEvent motionEvent = new MotionListener.MotionEvent(StateTypes.PRE);
        Vandalism.getInstance().getEventSystem().postInternal(MotionListener.MotionEvent.ID, motionEvent);
    }

    @Inject(method = "sendMovementPackets()V", at = @At("TAIL"))
    private void callMotionListener_post(final CallbackInfo ci) {
        final MotionListener.MotionEvent motionEvent = new MotionListener.MotionEvent(StateTypes.POST);
        Vandalism.getInstance().getEventSystem().postInternal(MotionListener.MotionEvent.ID, motionEvent);
    }

}
