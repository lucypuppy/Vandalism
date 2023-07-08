package me.nekosarekawaii.foxglove.mixin.net.minecraft;

import me.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SharedConstants.class)
public abstract class MixinSharedConstants {

    @Inject(method = "isValidChar", at = @At("RETURN"), cancellable = true)
    private static void injectIsValidChar(final char chr, final CallbackInfoReturnable<Boolean> cir) {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().allowColorChar.getValue()) {
            if (chr == '\u00a7') cir.setReturnValue(true);
        }
    }

}
