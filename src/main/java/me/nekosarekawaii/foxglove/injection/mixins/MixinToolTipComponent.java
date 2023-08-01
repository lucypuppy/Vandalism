package me.nekosarekawaii.foxglove.injection.mixins;

import me.nekosarekawaii.foxglove.util.minecraft.inventory.tooltip.ITooltipData;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TooltipComponent.class)
public interface MixinToolTipComponent {

    @Inject(method = "of*", at = @At("HEAD"), cancellable = true)
    private static void of(final TooltipData data, final CallbackInfoReturnable<TooltipComponent> cir) {
        if (data instanceof final ITooltipData tooltipData)
            cir.setReturnValue(tooltipData.getComponent());
    }

}
