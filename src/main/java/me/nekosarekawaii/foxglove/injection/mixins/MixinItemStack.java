package me.nekosarekawaii.foxglove.injection.mixins;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.event.ToolTipListener;
import me.nekosarekawaii.foxglove.util.minecraft.inventory.tooltip.CompoundTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Redirect(method = "hasGlint", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;hasGlint(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean injectGetDisplayStacks(final Item instance, final ItemStack stack) {
        final NbtCompound nbt = stack.getNbt();
        return instance.hasGlint(stack) || (nbt != null && nbt.contains(Foxglove.getInstance().getCreativeTabRegistry().getClientsideGlint()));
    }

    @Inject(method = "getTooltipData", at = @At("RETURN"), cancellable = true)
    private void onTooltipData(final CallbackInfoReturnable<Optional<TooltipData>> cir) {
        final List<TooltipData> tooltipData = new ArrayList<>();
        cir.getReturnValue().ifPresent(tooltipData::add);

        DietrichEvents2.global().postInternal(ToolTipListener.ToolTipEvent.ID,
                new ToolTipListener.ToolTipEvent((ItemStack) (Object) this, tooltipData));

        if (tooltipData.size() == 1) {
            cir.setReturnValue(Optional.of(tooltipData.get(0)));
        } else if (tooltipData.size() > 1) {
            final CompoundTooltipComponent comp = new CompoundTooltipComponent();

            for (var data : tooltipData) {
                comp.addComponent(TooltipComponent.of(data));
            }

            cir.setReturnValue(Optional.of(comp));
        }
    }

}