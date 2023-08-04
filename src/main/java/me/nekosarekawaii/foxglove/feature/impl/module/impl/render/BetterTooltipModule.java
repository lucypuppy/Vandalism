package me.nekosarekawaii.foxglove.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.TooltipListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.util.minecraft.inventory.tooltip.BannerTooltipComponent;
import me.nekosarekawaii.foxglove.util.minecraft.inventory.tooltip.ContainerTooltipComponent;
import me.nekosarekawaii.foxglove.util.minecraft.inventory.tooltip.MapTooltipComponent;
import me.nekosarekawaii.foxglove.util.minecraft.inventory.tooltip.SignTooltipComponent;
import me.nekosarekawaii.foxglove.util.render.ColorUtils;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BannerPatterns;
import net.minecraft.client.item.TooltipData;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

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
        final ItemStack itemStack = event.itemStack;
        final Item item = itemStack.getItem();
        final List<TooltipData> tooltipData = event.tooltipData;
        final String itemId = item.toString();

        if (itemId.endsWith("sign")) {
            SignTooltipComponent.fromItemStack(itemStack).ifPresent(tooltipData::add);
        } else if (item instanceof BannerPatternItem patternItem) {
            final boolean present =
                    Registries.BANNER_PATTERN.getEntryList(patternItem.getPattern()).isPresent() &&
                    Registries.BANNER_PATTERN.getEntryList(patternItem.getPattern()).get().size() != 0;
            final RegistryEntry<BannerPattern> bannerPattern = (
                    present ?
                            Registries.BANNER_PATTERN.getEntryList(patternItem.getPattern()).get().get(0) : null
            );

            if (bannerPattern != null) {
                final ItemStack bannerItem = new ItemStack(Items.GRAY_BANNER);
                final NbtCompound nbt = bannerItem.getOrCreateSubNbt("BlockEntityTag");
                final NbtList listNbt = new BannerPattern.Patterns()
                        .add(BannerPatterns.BASE, DyeColor.BLACK).add(bannerPattern, DyeColor.WHITE).toNbt();
                nbt.put("Patterns", listNbt);

                tooltipData.add(new BannerTooltipComponent(bannerItem));
            }
        } else if (item instanceof BannerItem) {
            tooltipData.add(new BannerTooltipComponent(itemStack));
        } else if (item == Items.FILLED_MAP) {
            final Integer mapId = FilledMapItem.getMapId(itemStack);

            if (mapId != null)
                tooltipData.add(new MapTooltipComponent(mapId));
        } else {
            final NbtCompound compoundTag = itemStack.getSubNbt("BlockEntityTag");

            if (compoundTag == null || !compoundTag.contains("Items", 9))
                return;

            float[] color = new float[]{1f, 1f, 1f};
            if (itemId.endsWith("shulker_box"))
                color = ColorUtils.getShulkerColor(itemStack);

            final DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
            Inventories.readNbt(compoundTag, itemStacks);
            tooltipData.add(new ContainerTooltipComponent(itemStacks, ColorUtils.withAlpha(color, 1f)));
        }
    }

}
