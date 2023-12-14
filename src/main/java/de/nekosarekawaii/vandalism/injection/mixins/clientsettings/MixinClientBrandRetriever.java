package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import de.nekosarekawaii.vandalism.Vandalism;
import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientBrandRetriever.class, priority = 9999)
public abstract class MixinClientBrandRetriever {

    @Inject(method = "getClientModName", at = @At("HEAD"), cancellable = true, remap = false)
    private static void vandalism$changeBrand(final CallbackInfoReturnable<String> cir) {
        if (Vandalism.getInstance() != null && Vandalism.getInstance().getConfigManager() != null && Vandalism.getInstance().getClientSettings() != null && Vandalism.getInstance().getClientSettings().getNetworkingSettings().changeBrand.getValue()) {
            cir.setReturnValue(Vandalism.getInstance().getClientSettings().getNetworkingSettings().brand.getValue());
        }
    }

}
