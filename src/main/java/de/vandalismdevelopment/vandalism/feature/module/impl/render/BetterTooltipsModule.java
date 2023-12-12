package de.vandalismdevelopment.vandalism.feature.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.common.ColorUtils;
import de.florianmichael.rclasses.common.StringUtils;
import de.florianmichael.rclasses.io.ByteCountDataOutput;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.base.event.render.CameraClipRaytraceListener;
import de.vandalismdevelopment.vandalism.base.event.render.TooltipDrawListener;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.util.tooltip.*;
import net.minecraft.block.ShulkerBoxBlock;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class BetterTooltipsModule extends AbstractModule implements TooltipDrawListener {

    public BetterTooltipsModule() {
        super("Better Tooltips", "Improves item tooltips from the game.", Category.RENDER);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(TooltipDrawEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(TooltipDrawEvent.ID, this);
    }

    @Override
    public void onTooltipDraw(final TooltipDrawEvent event) {
        final List<TooltipData> tooltipData = event.tooltipData;
        final ItemStack itemStack = event.itemStack;
        final Item item = itemStack.getItem();
        final String itemId = item.toString();
        if (item instanceof CompassItem && CompassItem.hasLodestone(itemStack)) {
            final NbtCompound nbt = itemStack.getNbt();
            if (nbt == null) {
                return;
            }
            final GlobalPos globalPos = CompassItem.createLodestonePos(nbt);
            if (globalPos == null) {
                return;
            }
            final BlockPos pos = globalPos.getPos();
            final Text posText = Text.literal(String.format("X: %d, Y: %d, Z: %d", pos.getX(), pos.getY(), pos.getZ())).formatted(Formatting.GOLD);
            final Text position = Text.literal("Position: ").formatted(Formatting.GRAY).append(posText);
            final Text dimension = Text.literal("Dimension: ").formatted(Formatting.GRAY).append(Text.literal(globalPos.getDimension().getValue().toString()).formatted(Formatting.GOLD));
            tooltipData.add(new TextTooltipComponent(position.asOrderedText()));
            tooltipData.add(new TextTooltipComponent(dimension.asOrderedText()));
        } else if (itemId.endsWith("sign")) {
            SignTooltipComponent.fromItemStack(itemStack).ifPresent(tooltipData::add);
        } else if (item instanceof BannerPatternItem patternItem) {
            final Optional<RegistryEntryList.Named<BannerPattern>> optionalList = Registries.BANNER_PATTERN.getEntryList(patternItem.getPattern());
            if (optionalList.isPresent()) {
                final RegistryEntryList.Named<BannerPattern> list = optionalList.get();
                final RegistryEntry<BannerPattern> bannerPattern = (list.size() > 0 ? list.get(0) : null);
                if (bannerPattern != null) {
                    final ItemStack bannerItem = new ItemStack(Items.GRAY_BANNER);
                    bannerItem.getOrCreateSubNbt("BlockEntityTag").put("Patterns", new BannerPattern.Patterns().add(BannerPatterns.BASE, DyeColor.BLACK).add(bannerPattern, DyeColor.WHITE).toNbt());
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
            if (compoundTag != null && compoundTag.contains("Items", 9)) {
                Color color = Color.WHITE;
                if (itemId.endsWith("shulker_box")) {
                    final DyeColor dye = ((ShulkerBoxBlock) ShulkerBoxBlock.getBlockFromItem(item)).getColor();
                    if (dye != null) {
                        final float[] dyeColor = dye.getColorComponents();
                        if (dyeColor.length == 3) {
                            color = new Color(dyeColor[0], dyeColor[1], dyeColor[2]);
                        }
                    }
                }
                final DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
                Inventories.readNbt(compoundTag, itemStacks);
                tooltipData.add(new TextTooltipComponent(Text.literal("(Press alt + middle click to open inventory)").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)).asOrderedText()));
                tooltipData.add(new ContainerTooltipComponent(itemStacks, ColorUtils.withAlpha(color, 1)));
            }
        }
        try {
            event.itemStack.writeNbt(new NbtCompound()).write(ByteCountDataOutput.INSTANCE);
            final int byteCount = ByteCountDataOutput.INSTANCE.getCount();
            ByteCountDataOutput.INSTANCE.reset();
            tooltipData.add(new TextTooltipComponent(Text.literal(StringUtils.formatBytes(byteCount)).formatted(Formatting.GRAY).asOrderedText()));
        } catch (final IOException e) {
            Vandalism.getInstance().getLogger().error("Failed to write item stack to nbt.", e);
        }
    }

}
