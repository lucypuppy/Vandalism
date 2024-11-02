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
import de.nekosarekawaii.vandalism.event.player.AttackListener;
import de.nekosarekawaii.vandalism.event.player.BlockBreakListener;
import de.nekosarekawaii.vandalism.event.player.PreBlockBreakListener;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {

    @Inject(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;syncSelectedSlot()V", shift = At.Shift.AFTER))
    private void callAttackListener(final PlayerEntity player, final Entity target, final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().callExceptionally(AttackListener.AttackSendEvent.ID, new AttackListener.AttackSendEvent(target));
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;syncSelectedSlot()V", shift = At.Shift.BEFORE), cancellable = true)
    private void callPreBlockBreakListener(final BlockPos pos, final Direction direction, final CallbackInfoReturnable<Boolean> cir) {
        final PreBlockBreakListener.PreBlockBreakEvent event = new PreBlockBreakListener.PreBlockBreakEvent(pos, direction);
        Vandalism.getInstance().getEventSystem().callExceptionally(PreBlockBreakListener.PreBlockBreakEvent.ID, event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "attackBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V", ordinal = 0), cancellable = true)
    private void callBlockBreakListenerStart(final BlockPos pos, final Direction direction, final CallbackInfoReturnable<Boolean> cir) {
        final BlockBreakListener.BlockBreakEvent event = new BlockBreakListener.BlockBreakEvent(pos, direction, BlockBreakListener.BlockBreakState.START);
        Vandalism.getInstance().getEventSystem().callExceptionally(BlockBreakListener.BlockBreakEvent.ID, event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "attackBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V", ordinal = 1), cancellable = true)
    private void callBlockBreakListenerStart2(final BlockPos pos, final Direction direction, final CallbackInfoReturnable<Boolean> cir) {
        final BlockBreakListener.BlockBreakEvent event = new BlockBreakListener.BlockBreakEvent(pos, direction, BlockBreakListener.BlockBreakState.START);
        Vandalism.getInstance().getEventSystem().callExceptionally(BlockBreakListener.BlockBreakEvent.ID, event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V", ordinal = 0), cancellable = true)
    private void callBlockBreakListenerStart3(final BlockPos pos, final Direction direction, final CallbackInfoReturnable<Boolean> cir) {
        final BlockBreakListener.BlockBreakEvent event = new BlockBreakListener.BlockBreakEvent(pos, direction, BlockBreakListener.BlockBreakState.START);
        Vandalism.getInstance().getEventSystem().callExceptionally(BlockBreakListener.BlockBreakEvent.ID, event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V", ordinal = 1), cancellable = true)
    private void callBlockBreakListenerStop(final BlockPos pos, final Direction direction, final CallbackInfoReturnable<Boolean> cir) {
        final BlockBreakListener.BlockBreakEvent event = new BlockBreakListener.BlockBreakEvent(pos, direction, BlockBreakListener.BlockBreakState.STOP);
        Vandalism.getInstance().getEventSystem().callExceptionally(BlockBreakListener.BlockBreakEvent.ID, event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "attackBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"), cancellable = true)
    private void callBlockBreakListenerAbort(final BlockPos pos, final Direction direction, final CallbackInfoReturnable<Boolean> cir) {
        final BlockBreakListener.BlockBreakEvent event = new BlockBreakListener.BlockBreakEvent(pos, direction, BlockBreakListener.BlockBreakState.ABORT);
        Vandalism.getInstance().getEventSystem().callExceptionally(BlockBreakListener.BlockBreakEvent.ID, event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = "cancelBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private void callBlockBreakListenerAbort2(final ClientPlayNetworkHandler instance, final Packet packet) {
        if (packet instanceof final PlayerActionC2SPacket playerActionC2SPacket) {
            final BlockBreakListener.BlockBreakEvent event = new BlockBreakListener.BlockBreakEvent(playerActionC2SPacket.getPos(), playerActionC2SPacket.getDirection(), BlockBreakListener.BlockBreakState.ABORT);
            Vandalism.getInstance().getEventSystem().callExceptionally(BlockBreakListener.BlockBreakEvent.ID, event);
            if (event.isCancelled()) {
                return;
            }
        }
        instance.sendPacket(packet);
    }

}
