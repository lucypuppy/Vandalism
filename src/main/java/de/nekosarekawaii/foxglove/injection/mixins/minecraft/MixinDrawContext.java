package de.nekosarekawaii.foxglove.injection.mixins.minecraft;

import de.nekosarekawaii.foxglove.util.inventory.tooltip.ITooltipData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DrawContext.class)
public abstract class MixinDrawContext {

    @Inject(method = "method_51442", at = @At(value = "HEAD"), cancellable = true)
    private static void injectDrawTooltip(final List<TooltipComponent> list, final TooltipData data, final CallbackInfo ci) {
        if (data instanceof final ITooltipData tooltipData && !tooltipData.renderPre()) {
            list.add(TooltipComponent.of(data));
            ci.cancel();
        }
    }

}
