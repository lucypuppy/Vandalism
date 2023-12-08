package de.vandalismdevelopment.vandalism.injection.mixins.feature.module;

import de.vandalismdevelopment.vandalism.Vandalism;
import net.fabricmc.fabric.impl.networking.client.ClientPlayNetworkAddon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayNetworkAddon.class, remap = false)
public abstract class MixinClientPlayNetworkAddon {

    @Inject(method = { "invokeRegisterEvent", "invokeUnregisterEvent", "handleRegistration", "handleUnregistration", "onServerReady" }, at = @At("HEAD"), cancellable = true)
    private void vandalism$modPacketBlockerFabric(final CallbackInfo ci) {
        final var modPacketBlockerModule = Vandalism.getInstance().getModuleRegistry().getModPacketBlockerModule();

        if (modPacketBlockerModule.isEnabled() && modPacketBlockerModule.unloadFabricAPICallbacks.getValue()) {
            ci.cancel();
        }
    }

}
