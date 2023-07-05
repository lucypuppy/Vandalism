package me.nekosarekawaii.foxglove.mixin.net.minecraft.client;

import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.misc.BrandChangerModule;
import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientBrandRetriever.class, priority = 9999)
public abstract class MixinClientBrandRetriever {

    @Inject(method = "getClientModName", at = @At("RETURN"), cancellable = true)
    private static void redirectGetClientModName(CallbackInfoReturnable<String> cir) {
        if (Foxglove.getInstance() != null && Foxglove.getInstance().getModuleRegistry() != null) {
            final BrandChangerModule brandChangerModule = Foxglove.getInstance().getModuleRegistry().getBrandChangerModule();
            if (brandChangerModule.isEnabled()) cir.setReturnValue(brandChangerModule.brand.getValue());
        }
    }

}
