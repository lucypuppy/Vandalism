package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import de.vandalismdevelopment.vandalism.Vandalism;
import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SharedConstants.class)
public abstract class MixinSharedConstants {

    @Inject(method = "isValidChar", at = @At("RETURN"), cancellable = true)
    private static void injectIsValidChar(final char chr, final CallbackInfoReturnable<Boolean> cir) {
        if (Vandalism.getInstance().getConfigManager().getMainConfig().chatCategory.allowColorChar.getValue()) {
            if (chr == '§') cir.setReturnValue(true);
        }
    }

}
