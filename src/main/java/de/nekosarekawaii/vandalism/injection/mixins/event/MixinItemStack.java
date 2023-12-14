package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.vandalism.base.event.render.TooltipDrawListener;
import de.nekosarekawaii.vandalism.util.tooltip.CompoundTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Inject(method = "getTooltipData", at = @At("RETURN"), cancellable = true)
    private void vandalism$callTooltipDrawEvent(final CallbackInfoReturnable<Optional<TooltipData>> cir) {
        final List<TooltipData> tooltipData = new ArrayList<>();
        cir.getReturnValue().ifPresent(tooltipData::add);
        DietrichEvents2.global().postInternal(TooltipDrawListener.TooltipDrawEvent.ID, new TooltipDrawListener.TooltipDrawEvent(
                (ItemStack) (Object) this, tooltipData)
        );
        if (tooltipData.size() == 1) {
            cir.setReturnValue(Optional.of(tooltipData.get(0)));
        } else if (tooltipData.size() > 1) {
            final CompoundTooltipComponent comp = new CompoundTooltipComponent();
            for (final TooltipData data : tooltipData) {
                comp.addComponent(TooltipComponent.of(data));
            }
            cir.setReturnValue(Optional.of(comp));
        }
    }

}