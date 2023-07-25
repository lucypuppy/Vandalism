package me.nekosarekawaii.foxglove.mixin.net.minecraft.client;

import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.exploit.BrandChangerModule;
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
    private static void redirectGetClientModName(CallbackInfoReturnable<String> cir) {
        if (Foxglove.getInstance() != null && Foxglove.getInstance().getModuleRegistry() != null) {
            final BrandChangerModule brandChangerModule = Foxglove.getInstance().getModuleRegistry().getBrandChangerModule();
            if (brandChangerModule.isEnabled()) {
                cir.setReturnValue(brandChangerModule.brand.getValue());
            }
        }
        cir.setReturnValue(VANILLA);
    }

}
