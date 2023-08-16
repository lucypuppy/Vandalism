package de.nekosarekawaii.foxglove.injection.mixins;

import de.nekosarekawaii.foxglove.util.minecraft.inventory.ScreenHandlerTypes;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenHandlerType.class)
public abstract class MixinScreenHandlerType {


    @Inject(method = "register(Ljava/lang/String;Lnet/minecraft/screen/ScreenHandlerType$Factory;)Lnet/minecraft/screen/ScreenHandlerType;", at = @At("RETURN"))
    private static void injectRegister(final String id, final ScreenHandlerType.Factory<ScreenHandler> factory, final CallbackInfoReturnable<ScreenHandlerType<ScreenHandler>> cir) {
        ScreenHandlerTypes.registerType(cir.getReturnValue(), id);
    }

    @Inject(method = "register(Ljava/lang/String;Lnet/minecraft/screen/ScreenHandlerType$Factory;[Lnet/minecraft/resource/featuretoggle/FeatureFlag;)Lnet/minecraft/screen/ScreenHandlerType;", at = @At("RETURN"))
    private static void injectRegister(final String id, final ScreenHandlerType.Factory<ScreenHandler> factory, final FeatureFlag[] requiredFeatures, final CallbackInfoReturnable<ScreenHandlerType<ScreenHandler>> cir) {
        ScreenHandlerTypes.registerType(cir.getReturnValue(), id);
    }

}
