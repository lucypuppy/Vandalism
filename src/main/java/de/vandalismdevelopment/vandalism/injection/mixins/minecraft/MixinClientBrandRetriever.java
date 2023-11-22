package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.exploit.ClientBrandChangerModule;
import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientBrandRetriever.class, priority = 9999)
public abstract class MixinClientBrandRetriever {

    @Inject(method = "getClientModName", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectGetClientModName(final CallbackInfoReturnable<String> cir) {
        if (Vandalism.getInstance() != null && Vandalism.getInstance().getModuleRegistry() != null) {
            final ClientBrandChangerModule clientBrandChangerModule = Vandalism.getInstance().getModuleRegistry().getClientBrandChangerModule();
            if (clientBrandChangerModule.isEnabled()) cir.setReturnValue(clientBrandChangerModule.brand.getValue());
        }
    }

}
