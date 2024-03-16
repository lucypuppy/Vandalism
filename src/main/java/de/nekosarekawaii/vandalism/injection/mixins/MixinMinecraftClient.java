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

package de.nekosarekawaii.vandalism.injection.mixins;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.util.game.ServerConnectionUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.management.ManagementFactory;

@Mixin(value = MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow @Nullable
    public abstract ClientPlayNetworkHandler getNetworkHandler();

    @Unique
    private boolean vandalism$loadingDisplayed = false;

    @Inject(method = "onFinishedLoading", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;collectLoadTimes(Lnet/minecraft/client/MinecraftClient$LoadingContext;)V", shift = At.Shift.AFTER))
    private void displayLoadingTime(final CallbackInfo ci) {
        if (!this.vandalism$loadingDisplayed) {
            this.vandalism$loadingDisplayed = true;
            Vandalism.getInstance().getLogger().info("");
            Vandalism.getInstance().getLogger().info("Minecraft loading took ~" + ManagementFactory.getRuntimeMXBean().getUptime() + "ms.");
            Vandalism.getInstance().getLogger().info("");
        }
    }

    @Inject(method = "updateWindowTitle", at = @At("HEAD"), cancellable = true)
    private void forceCancelWindowTitleUpdates(final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "doAttack", at = @At("HEAD"))
    public void doAttack(final CallbackInfoReturnable<Boolean> cir) {
        Vandalism.getInstance().getHudManager().infoHUDElement.leftClick.click();
    }

    @Inject(method = "doItemUse", at = @At("HEAD"))
    public void doItemUse(final CallbackInfo ci) {
        Vandalism.getInstance().getHudManager().infoHUDElement.rightClick.click();
    }

    @Inject(method = "setScreen", at = @At(value = "INVOKE", target = "Ljava/lang/IllegalStateException;<init>(Ljava/lang/String;)V", shift = At.Shift.BEFORE, remap = false), cancellable = true)
    private void fixISE(final Screen screen, final CallbackInfo ci) {
        ci.cancel();
        ServerConnectionUtil.disconnect("Trying to return to in-game GUI during disconnection.");
    }

}