package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import de.nekosarekawaii.vandalism.Vandalism;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemGroup.class)
public abstract class MixinItemGroup {

    @Inject(method = "shouldDisplay", at = @At("HEAD"), cancellable = true)
    private void alwaysDisplayCreativeTab(final CallbackInfoReturnable<Boolean> cir) {
        if (Vandalism.getInstance().getClientSettings().getMenuSettings().alwaysDisplayCreativeTab.getValue()) {
            cir.setReturnValue(true);
        }
    }

}
