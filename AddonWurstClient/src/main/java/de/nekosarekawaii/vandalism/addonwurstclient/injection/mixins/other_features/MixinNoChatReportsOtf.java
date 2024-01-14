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

package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins.other_features;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
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
    private void removeWurstNoChatReportsDisableSignaturesSetting(final NoChatReportsOtf instance, final Setting setting) {}

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/event/Event;register(Ljava/lang/Object;)V"))
    private void removeWurstNoChatReportsClientLoginConnectionEventRegister(final Event instance, final Object t) {}

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    private void cancelWurstNoChatReportsUpdateEvent(final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "onLoginStart", at = @At("HEAD"), cancellable = true)
    private void cancelWurstNoChatReportsLoginStartEvent(final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "isEnabled", at = @At("HEAD"), cancellable = true)
    private void forceDisableWurstNoChatReports(final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "isActive", at = @At("HEAD"), cancellable = true)
    private void forceDeactivateWurstNoChatReports(final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "doPrimaryAction", at = @At("HEAD"), cancellable = true)
    private void cancelWurstNoChatReportsPrimaryAction(final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "modifyIndicator", at = @At("HEAD"), cancellable = true)
    private void cancelWurstNoChatReportsModifyIndicator(final Text message, final MessageSignatureData signature, final MessageIndicator indicator, final CallbackInfoReturnable<MessageIndicator> cir) {
        cir.setReturnValue(indicator);
    }

}
