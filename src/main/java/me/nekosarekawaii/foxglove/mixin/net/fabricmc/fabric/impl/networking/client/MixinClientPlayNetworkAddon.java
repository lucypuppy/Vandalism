package me.nekosarekawaii.foxglove.mixin.net.fabricmc.fabric.impl.networking.client;

import net.fabricmc.fabric.impl.networking.client.ClientPlayNetworkAddon;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ClientPlayNetworkAddon.class, remap = false)
public abstract class MixinClientPlayNetworkAddon {

    //TODO: Fix this correct so that it doesn't make connection issues with servers.

    /*@Inject(method = "invokeRegisterEvent", at = @At("HEAD"), cancellable = true)
    private void injectInvokeRegisterEvent(final CallbackInfo ci) {
        final ModPacketBlockerModule modPacketBlockerModule = Foxglove.getInstance().getModuleRegistry().getModPacketBlockerModule();
        if (modPacketBlockerModule.isEnabled() && modPacketBlockerModule.fabric.getValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "invokeUnregisterEvent", at = @At("HEAD"), cancellable = true)
    private void injectInvokeUnregisterEvent(final CallbackInfo ci) {
        final ModPacketBlockerModule modPacketBlockerModule = Foxglove.getInstance().getModuleRegistry().getModPacketBlockerModule();
        if (modPacketBlockerModule.isEnabled() && modPacketBlockerModule.fabric.getValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "handleRegistration", at = @At("HEAD"), cancellable = true)
    private void injectHandleRegistration(final Identifier channelName, final CallbackInfo ci) {
        final ModPacketBlockerModule modPacketBlockerModule = Foxglove.getInstance().getModuleRegistry().getModPacketBlockerModule();
        if (modPacketBlockerModule.isEnabled() && modPacketBlockerModule.fabric.getValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "handleUnregistration", at = @At("HEAD"), cancellable = true)
    private void injectHandleUnregistration(final Identifier channelName, final CallbackInfo ci) {
        final ModPacketBlockerModule modPacketBlockerModule = Foxglove.getInstance().getModuleRegistry().getModPacketBlockerModule();
        if (modPacketBlockerModule.isEnabled() && modPacketBlockerModule.fabric.getValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "onServerReady", at = @At("HEAD"), cancellable = true)
    private void injectOnServerReady(final CallbackInfo ci) {
        final ModPacketBlockerModule modPacketBlockerModule = Foxglove.getInstance().getModuleRegistry().getModPacketBlockerModule();
        if (modPacketBlockerModule.isEnabled() && modPacketBlockerModule.fabric.getValue()) {
            ci.cancel();
        }
    }*/

}
