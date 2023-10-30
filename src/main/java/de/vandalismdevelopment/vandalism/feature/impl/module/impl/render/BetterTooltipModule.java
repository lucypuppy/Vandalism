package de.vandalismdevelopment.vandalism.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.TooltipListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.util.inventory.tooltip.*;
import de.vandalismdevelopment.vandalism.util.render.ColorUtil;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BannerPatterns;
import net.minecraft.client.item.TooltipData;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.Optional;

public class BetterTooltipModule extends Module implements TooltipListener {

    public BetterTooltipModule() {
        super(
                "Better Tooltip",
                "Improves item tooltips from the game.",
                FeatureCategory.RENDER,
                false,
                false
        );
    }

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
            final Optional<RegistryEntryList.Named<BannerPattern>> optionalList = Registries.BANNER_PATTERN.getEntryList(patternItem.getPattern());
            if (optionalList.isPresent()) {
                final RegistryEntryList.Named<BannerPattern> list = optionalList.get();
                final RegistryEntry<BannerPattern> bannerPattern = (list.size() > 0 ? list.get(0) : null);
                if (bannerPattern != null) {
                    final ItemStack bannerItem = new ItemStack(Items.GRAY_BANNER);
                    bannerItem.getOrCreateSubNbt("BlockEntityTag").put("Patterns", new BannerPattern.Patterns()
                            .add(BannerPatterns.BASE, DyeColor.BLACK)
                            .add(bannerPattern, DyeColor.WHITE)
                            .toNbt());
                    tooltipData.add(new BannerTooltipComponent(bannerItem));
                }
            }
        } else if (item instanceof BannerItem) {
            tooltipData.add(new BannerTooltipComponent(itemStack));
        } else if (item == Items.FILLED_MAP) {
            final Integer mapId = FilledMapItem.getMapId(itemStack);
            if (mapId != null) {
                tooltipData.add(new MapTooltipComponent(mapId));
            }
        } else {
            final NbtCompound compoundTag = itemStack.getSubNbt("BlockEntityTag");

            if (compoundTag == null || !compoundTag.contains("Items", 9)) {
                return;
            }

            float[] color = new float[]{1f, 1f, 1f};
            if (itemId.endsWith("shulker_box")) {
                color = ColorUtil.getShulkerColor(itemStack);
            }

            final DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
            Inventories.readNbt(compoundTag, itemStacks);

            tooltipData.add(new TextTooltipComponent(
                    Text.literal("(Press alt + middle click to open inventory)")
                            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
                            .asOrderedText())
            );
            tooltipData.add(new ContainerTooltipComponent(itemStacks, ColorUtil.withAlpha(color, 1f)));
        }
    }

}
