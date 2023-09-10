package de.nekosarekawaii.foxglove.injection.mixins.minecraft;

import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.feature.impl.module.impl.exploit.ClientBrandChangerModule;
import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientBrandRetriever.class, priority = 9999)
public abstract class MixinClientBrandRetriever {

    @Shadow
    @Final
    public static String VANILLA;

    @Inject(method = "getClientModName", at = @At("RETURN"), cancellable = true, remap = false)
    private static void injectGetClientModName(CallbackInfoReturnable<String> cir) {
        if (Foxglove.getInstance() != null && Foxglove.getInstance().getModuleRegistry() != null) {
            final ClientBrandChangerModule clientBrandChangerModule = Foxglove.getInstance().getModuleRegistry().getClientBrandChangerModule();
            if (clientBrandChangerModule.isEnabled()) {
                cir.setReturnValue(clientBrandChangerModule.brand.getValue());
            }
        }
        cir.setReturnValue(VANILLA);
    }

}
