package de.vandalismdevelopment.vandalism.injection.mixins.libs.fabric;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.misc.ModPacketBlockerModule;
import net.fabricmc.fabric.impl.networking.client.ClientPlayNetworkAddon;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayNetworkAddon.class, remap = false)
public abstract class MixinClientPlayNetworkAddon {

    @Inject(method = "invokeRegisterEvent", at = @At("HEAD"), cancellable = true)
    private void injectInvokeRegisterEvent(final CallbackInfo ci) {
        final ModPacketBlockerModule modPacketBlockerModule = Vandalism.getInstance().getModuleRegistry().getModPacketBlockerModule();
        if (modPacketBlockerModule.isEnabled() && modPacketBlockerModule.fabric.getValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "invokeUnregisterEvent", at = @At("HEAD"), cancellable = true)
    private void injectInvokeUnregisterEvent(final CallbackInfo ci) {
        final ModPacketBlockerModule modPacketBlockerModule = Vandalism.getInstance().getModuleRegistry().getModPacketBlockerModule();
        if (modPacketBlockerModule.isEnabled() && modPacketBlockerModule.fabric.getValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "handleRegistration", at = @At("HEAD"), cancellable = true)
    private void injectHandleRegistration(final Identifier channelName, final CallbackInfo ci) {
        final ModPacketBlockerModule modPacketBlockerModule = Vandalism.getInstance().getModuleRegistry().getModPacketBlockerModule();
        if (modPacketBlockerModule.isEnabled() && modPacketBlockerModule.fabric.getValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "handleUnregistration", at = @At("HEAD"), cancellable = true)
    private void injectHandleUnregistration(final Identifier channelName, final CallbackInfo ci) {
        final ModPacketBlockerModule modPacketBlockerModule = Vandalism.getInstance().getModuleRegistry().getModPacketBlockerModule();
        if (modPacketBlockerModule.isEnabled() && modPacketBlockerModule.fabric.getValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "onServerReady", at = @At("HEAD"), cancellable = true)
    private void injectOnServerReady(final CallbackInfo ci) {
        final ModPacketBlockerModule modPacketBlockerModule = Vandalism.getInstance().getModuleRegistry().getModPacketBlockerModule();
        if (modPacketBlockerModule.isEnabled() && modPacketBlockerModule.fabric.getValue()) {
            ci.cancel();
        }
    }

}
