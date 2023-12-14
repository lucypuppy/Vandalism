package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import de.nekosarekawaii.vandalism.Vandalism;
import net.minecraft.util.StringHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StringHelper.class)
public abstract class MixinStringHelper {

    @Inject(method = "truncateChat", at = @At(value = "HEAD"), cancellable = true)
    private static void vandalism$customChatLength(final String text, final CallbackInfoReturnable<String> cir) {
        if (Vandalism.getInstance().getClientSettings().getChatSettings().customChatLength.getValue()) {
            cir.setReturnValue(text);
        }
    }

}
