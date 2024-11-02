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

package de.nekosarekawaii.vandalism.injection.mixins.fix.minecraft;

import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.AxisGridWidget;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(OptionsScreen.class)
public abstract class MixinOptionsScreen {

    @Inject(method = "lockDifficulty", at = @At("HEAD"), cancellable = true)
    private void removeLockDifficultyFunction(final boolean difficultyLocked, final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "createTopRightButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/AxisGridWidget;add(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;", ordinal = 1, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void removeLockDifficultyButton(final CallbackInfoReturnable<Widget> cir, final AxisGridWidget axisGridWidget) {
        axisGridWidget.elements.remove(axisGridWidget.elements.size() - 1);
    }

    @Redirect(method = "createTopRightButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/LockButtonWidget;getWidth()I"))
    private int fixDifficultyButtonWidth(final LockButtonWidget instance) {
        return 0;
    }

    @Redirect(method = "createTopRightButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld$Properties;isDifficultyLocked()Z"))
    private boolean fixDifficultyButtonWidth(final ClientWorld.Properties instance) {
        return false;
    }

    @Redirect(method = "method_39487", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private static void fixDifficultyPacket(final ClientPlayNetworkHandler instance, final Packet packet) {
        instance.sendPacket(new UpdateDifficultyLockC2SPacket(false));
        instance.sendPacket(packet);
    }

}
