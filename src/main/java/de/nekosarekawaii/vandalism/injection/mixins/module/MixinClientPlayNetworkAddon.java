package de.nekosarekawaii.vandalism.injection.mixins.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.ModPacketBlockerModule;
import net.fabricmc.fabric.impl.networking.client.ClientPlayNetworkAddon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayNetworkAddon.class, remap = false)
public abstract class MixinClientPlayNetworkAddon {

    @Inject(method = { "invokeRegisterEvent", "invokeUnregisterEvent", "handleRegistration", "handleUnregistration", "onServerReady" }, at = @At("HEAD"), cancellable = true)
    private void vandalism$modPacketBlockerFabric(final CallbackInfo ci) {
        final ModPacketBlockerModule modPacketBlockerModule = Vandalism.getInstance().getModuleManager().getModPacketBlockerModule();
        if (modPacketBlockerModule.isActive() && modPacketBlockerModule.unloadFabricAPICallbacks.getValue()) {
            ci.cancel();
        }
    }

}
