package de.nekosarekawaii.foxglove.injection.mixins;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.event.TooltipListener;
import de.nekosarekawaii.foxglove.util.minecraft.inventory.tooltip.CompoundTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
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

    @Inject(method = "getTooltip", at = @At(value = "RETURN"))
    private void onGetTooltip(final PlayerEntity player, final TooltipContext context, final CallbackInfoReturnable<List<Text>> cir) {
        final List<Text> tooltip = cir.getReturnValue();
        final ItemStack itemStack = (ItemStack) (Object) this;

        if (itemStack.getItem() instanceof CompassItem && CompassItem.hasLodestone(itemStack)) {
            var nbt = itemStack.getNbt();

            if (nbt == null)
                return;

            final GlobalPos globalPos = CompassItem.createLodestonePos(nbt);

            if (globalPos == null)
                return;

            final BlockPos pos = globalPos.getPos();
            var posText = Text.literal(String.format("X: %d, Y: %d, Z: %d", pos.getX(), pos.getY(), pos.getZ()))
                    .formatted(Formatting.GOLD);

            final Text position = Text.literal("Position: ").formatted(Formatting.GRAY).append(posText);
            final Text dimension = Text.literal("Dimension: ").formatted(Formatting.GRAY)
                    .append(Text.literal(globalPos.getDimension().getValue().toString()).formatted(Formatting.GOLD));

            if (context.isAdvanced()) {
                tooltip.add(tooltip.size() - 2, position);
                tooltip.add(tooltip.size() - 2, dimension);
            } else {
                tooltip.add(position);
                tooltip.add(dimension);
            }
        }
    }

    @Inject(method = "getTooltipData", at = @At("RETURN"), cancellable = true)
    private void injectTooltipData(final CallbackInfoReturnable<Optional<TooltipData>> cir) {
        final List<TooltipData> tooltipData = new ArrayList<>();
        cir.getReturnValue().ifPresent(tooltipData::add);

        DietrichEvents2.global().postInternal(TooltipListener.TooltipEvent.ID, new TooltipListener.TooltipEvent((ItemStack) (Object) this, tooltipData));

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