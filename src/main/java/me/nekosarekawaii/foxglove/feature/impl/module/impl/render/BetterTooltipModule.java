package me.nekosarekawaii.foxglove.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.TooltipListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.util.minecraft.inventory.tooltip.ContainerTooltipComponent;
import me.nekosarekawaii.foxglove.util.render.ColorUtils;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

@ModuleInfo(name = "Better Tooltip", description = "A module which improves the tooltip rendering.", category = FeatureCategory.RENDER)
public class BetterTooltipModule extends Module implements TooltipListener {

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(TooltipEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(TooltipEvent.ID, this);
    }

    @Override
    public void onTooltipData(final TooltipEvent event) {
        final NbtCompound compoundTag = event.itemStack.getSubNbt("BlockEntityTag");

        if (compoundTag == null || !compoundTag.contains("Items", 9))
            return;

        final DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
        Inventories.readNbt(compoundTag, itemStacks);
        event.tooltipData.add(new ContainerTooltipComponent(itemStacks,
                ColorUtils.withAlpha(ColorUtils.getShulkerColor(event.itemStack), 1f)));
    }

}
