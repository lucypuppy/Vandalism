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
import de.nekosarekawaii.vandalism.base.event.normal.player.PlayerSlowdownListener;
import de.nekosarekawaii.vandalism.base.event.normal.player.PlayerUpdateListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {

    @Shadow @Final protected MinecraftClient client;

    @Shadow
    public Input input;

    @Inject(method = "tick", at = @At("HEAD"))
    private void callPlayerUpdateListener_pre(final CallbackInfo ci) {
        if ((Object) this != this.client.player) {
            return;
        }
        final PlayerUpdateListener.PlayerUpdateEvent event = new PlayerUpdateListener.PlayerUpdateEvent(StateTypes.PRE);
        Vandalism.getInstance().getEventSystem().postInternal(PlayerUpdateListener.PlayerUpdateEvent.ID, event);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendMovementPackets()V", shift = At.Shift.AFTER))
    private void callPlayerUpdateListener_post(final CallbackInfo ci) {
        if ((Object) this != this.client.player) {
            return;
        }
        final PlayerUpdateListener.PlayerUpdateEvent event = new PlayerUpdateListener.PlayerUpdateEvent(StateTypes.POST);
        Vandalism.getInstance().getEventSystem().postInternal(PlayerUpdateListener.PlayerUpdateEvent.ID, event);
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z", ordinal = 0))
    private void hookCustomMultiplier(CallbackInfo callbackInfo) {
        final Input input = this.input;

        input.movementForward /= 0.2f;
        input.movementSideways /= 0.2f;

        final PlayerSlowdownListener.PlayerSlowdownEvent playerUseMultiplier = new PlayerSlowdownListener.PlayerSlowdownEvent(0.2f, 0.2f);
        Vandalism.getInstance().getEventSystem().postInternal(PlayerSlowdownListener.PlayerSlowdownEvent.ID, playerUseMultiplier);

        input.movementForward *= playerUseMultiplier.movementForward;
        input.movementSideways *= playerUseMultiplier.movementSideways;
    }

}
