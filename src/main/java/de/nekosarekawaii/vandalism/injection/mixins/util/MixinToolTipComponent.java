package de.nekosarekawaii.vandalism.injection.mixins.util;

import de.nekosarekawaii.vandalism.util.tooltip.ConvertibleTooltipData;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TooltipComponent.class)
public interface MixinToolTipComponent {

    @Inject(method = "of(Lnet/minecraft/client/item/TooltipData;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;", at = @At("HEAD"), cancellable = true)
    private static void applyTooltipComponentToTooltipData(final TooltipData data, final CallbackInfoReturnable<TooltipComponent> cir) {
        if (data instanceof final ConvertibleTooltipData tooltipData) {
            cir.setReturnValue(tooltipData.getComponent());
        }
    }

}
