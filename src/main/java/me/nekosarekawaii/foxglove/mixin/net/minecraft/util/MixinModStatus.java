package me.nekosarekawaii.foxglove.mixin.net.minecraft.util;

import net.minecraft.util.ModStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(ModStatus.class)
public abstract class MixinModStatus {

    @Inject(method = "isModded", at = @At("RETURN"), cancellable = true)
    private void injectIsModded(final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "check", at = @At("RETURN"), cancellable = true)
    private static void injectCheck(final String defaultBrand, final Supplier<String> brandSupplier, final String environment, final Class<?> clazz, final CallbackInfoReturnable<ModStatus> cir) {
        cir.setReturnValue(new ModStatus(ModStatus.Confidence.PROBABLY_NOT, environment + " jar signature and brand is untouched"));
    }

}
