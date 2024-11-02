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

package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins.other_features;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import net.wurstclient.event.EventManager;
import net.wurstclient.event.Listener;
import net.wurstclient.events.ChatInputListener;
import net.wurstclient.other_features.NoChatReportsOtf;
import net.wurstclient.settings.Setting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NoChatReportsOtf.class, remap = false)
public abstract class MixinNoChatReportsOtf {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/wurstclient/other_features/NoChatReportsOtf;addSetting(Lnet/wurstclient/settings/Setting;)V"))
    private void removeDisableSignaturesSetting(final NoChatReportsOtf instance, final Setting setting) {
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/event/Event;register(Ljava/lang/Object;)V"))
    private void cancelClientLoginConnectionEventsRegister(final Event instance, final Object t) {
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/wurstclient/event/EventManager;add(Ljava/lang/Class;Lnet/wurstclient/event/Listener;)V"))
    private <L extends Listener> void removeChatInputEvent(final EventManager instance, final Class<L> report, final L section) {
    }

    @Inject(method = "onUpdate", at = @At(value = "HEAD"), cancellable = true)
    private void cancelOnUpdate(final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "onReceivedMessage", at = @At(value = "HEAD"), cancellable = true)
    private void cancelOnReceiveMessage(final ChatInputListener.ChatInputEvent event, final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "onLoginStart", at = @At(value = "HEAD"), cancellable = true)
    private void cancelOnLoginStart(final ClientLoginNetworkHandler handler, final MinecraftClient client, final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "modifyIndicator", at = @At(value = "HEAD"), cancellable = true)
    private void doNotModifyTheIndicator(final Text message, final MessageSignatureData signature, final MessageIndicator indicator, final CallbackInfoReturnable<MessageIndicator> cir) {
        cir.setReturnValue(indicator);
    }

    @Inject(method = "isEnabled", at = @At(value = "RETURN"), cancellable = true)
    private void disableIsEnabled(final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "isActive", at = @At(value = "RETURN"), cancellable = true)
    private void disableIsActive(final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "doPrimaryAction", at = @At(value = "HEAD"), cancellable = true)
    private void dontDoPrimaryAction(final CallbackInfo ci) {
        ci.cancel();
    }

}
