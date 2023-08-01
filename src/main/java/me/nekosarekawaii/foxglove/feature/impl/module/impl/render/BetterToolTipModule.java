package me.nekosarekawaii.foxglove.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.ToolTipListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.util.minecraft.inventory.tooltip.ContainerTooltipComponent;
import me.nekosarekawaii.foxglove.util.render.ColorUtils;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

@ModuleInfo(name = "Better ToolTip", description = "A module which improves the tooltip rendering.", category = FeatureCategory.RENDER)
public class BetterToolTipModule extends Module implements ToolTipListener {

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(ToolTipEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(ToolTipEvent.ID, this);
    }

    @Override
    public void onTooltipData(final ToolTipEvent event) {
        final NbtCompound compoundTag = event.itemStack.getSubNbt("BlockEntityTag");

        if (compoundTag == null || !compoundTag.contains("Items", 9))
            return;

        final DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
        Inventories.readNbt(compoundTag, itemStacks);
        event.tooltipData.add(new ContainerTooltipComponent(itemStacks,
                ColorUtils.withAlpha(ColorUtils.getShulkerColor(event.itemStack), 1f)));
    }

}
