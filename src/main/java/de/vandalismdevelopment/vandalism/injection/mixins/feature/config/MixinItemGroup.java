package de.vandalismdevelopment.vandalism.injection.mixins.feature.config;

import de.vandalismdevelopment.vandalism.Vandalism;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemGroup.class)
public abstract class MixinItemGroup {

    @Inject(method = "shouldDisplay", at = @At("HEAD"), cancellable = true)
    private void vandalism$alwaysDisplayCreativeTab(final CallbackInfoReturnable<Boolean> cir) {
        if (Vandalism.getInstance().getClientSettings().getMenuSettings().alwaysDisplayCreativeTab.getValue()) {
            cir.setReturnValue(true);
        }
    }

}
