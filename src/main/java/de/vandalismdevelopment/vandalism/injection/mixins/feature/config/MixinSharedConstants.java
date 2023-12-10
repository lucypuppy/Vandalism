package de.vandalismdevelopment.vandalism.injection.mixins.feature.config;

import de.vandalismdevelopment.vandalism.Vandalism;
import net.minecraft.SharedConstants;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SharedConstants.class)
public abstract class MixinSharedConstants {

    @Inject(method = "isValidChar", at = @At("RETURN"), cancellable = true)
    private static void vandalism$allowColorChar(final char chr, final CallbackInfoReturnable<Boolean> cir) {
        if (Vandalism.getInstance().getClientSettings().getChatSettings().allowColorChar.getValue()) {
            if (chr == Formatting.FORMATTING_CODE_PREFIX) {
                cir.setReturnValue(true);
            }
        }
    }

}
