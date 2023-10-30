package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import de.vandalismdevelopment.vandalism.util.inventory.ScreenHandlerType;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.screen.ScreenHandlerType.class)
public abstract class MixinScreenHandlerType {


    @Inject(method = "register(Ljava/lang/String;Lnet/minecraft/screen/ScreenHandlerType$Factory;)Lnet/minecraft/screen/ScreenHandlerType;", at = @At("RETURN"))
    private static void injectRegister(final String id, final net.minecraft.screen.ScreenHandlerType.Factory<ScreenHandler> factory, final CallbackInfoReturnable<net.minecraft.screen.ScreenHandlerType<ScreenHandler>> cir) {
        ScreenHandlerType.registerType(cir.getReturnValue(), id);
    }

    @Inject(method = "register(Ljava/lang/String;Lnet/minecraft/screen/ScreenHandlerType$Factory;[Lnet/minecraft/resource/featuretoggle/FeatureFlag;)Lnet/minecraft/screen/ScreenHandlerType;", at = @At("RETURN"))
    private static void injectRegister(final String id, final net.minecraft.screen.ScreenHandlerType.Factory<ScreenHandler> factory, final FeatureFlag[] requiredFeatures, final CallbackInfoReturnable<net.minecraft.screen.ScreenHandlerType<ScreenHandler>> cir) {
        ScreenHandlerType.registerType(cir.getReturnValue(), id);
    }

}
