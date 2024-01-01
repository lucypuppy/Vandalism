package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins.render;

import net.wurstclient.options.WurstOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WurstOptionsScreen.class, remap = false)
public abstract class MixinWurstOptionsScreen {

    @Inject(method = "addSettingButtons", at = @At(value = "INVOKE", target = "Lnet/wurstclient/options/WurstOptionsScreen$WurstOptionsButton;<init>(Lnet/wurstclient/options/WurstOptionsScreen;IILjava/util/function/Supplier;Ljava/lang/String;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)V", ordinal = 1), cancellable = true)
    private void removeWurstCountUsersButton(final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "addManagerButtons", at = @At(value = "INVOKE", target = "Lnet/wurstclient/options/WurstOptionsScreen$WurstOptionsButton;<init>(Lnet/wurstclient/options/WurstOptionsScreen;IILjava/util/function/Supplier;Ljava/lang/String;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)V", ordinal = 2), cancellable = true)
    private void removeWurstZoomButton(final CallbackInfo ci) {
        ci.cancel();
    }

}
