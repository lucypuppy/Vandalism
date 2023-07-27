package me.nekosarekawaii.foxglove.injection.mixins;

import me.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.util.StringHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StringHelper.class)
public abstract class MixinStringHelper {

    @Inject(method = "truncateChat", at = @At(value = "HEAD"), cancellable = true)
    private static void injectTruncateChat(final String text, final CallbackInfoReturnable<String> cir) {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().customChatLength.getValue()) {
            cir.setReturnValue(text);
        }
    }

}
