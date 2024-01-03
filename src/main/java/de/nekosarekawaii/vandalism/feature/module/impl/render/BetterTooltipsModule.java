/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.feature.module.impl.render;

import de.florianmichael.rclasses.common.StringUtils;
import de.florianmichael.rclasses.io.ByteCountDataOutput;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.render.TooltipDrawListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.tooltip.impl.BannerTooltipComponent;
import de.nekosarekawaii.vandalism.util.tooltip.impl.ContainerTooltipComponent;
import de.nekosarekawaii.vandalism.util.tooltip.impl.MapTooltipComponent;
import de.nekosarekawaii.vandalism.util.tooltip.impl.TextTooltipComponent;
import net.minecraft.block.Block;
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
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(TooltipDrawEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(TooltipDrawEvent.ID, this);
    }

    @Override
    public void onTooltipDraw(final TooltipDrawEvent event) {
        final List<TooltipData> tooltipData = event.tooltipData;
        final ItemStack itemStack = event.itemStack;
        final Item item = itemStack.getItem();

        if (item instanceof CompassItem && CompassItem.hasLodestone(itemStack)) {
            drawCompassTooltip(tooltipData, itemStack);
        } else if (item instanceof BannerPatternItem patternItem) {
            drawBannerPatternTooltip(tooltipData, patternItem);
        } else if (item instanceof BannerItem) {
            tooltipData.add(new BannerTooltipComponent(itemStack));
        } else if (item == Items.FILLED_MAP) {
            final Integer mapId = FilledMapItem.getMapId(itemStack);
            if (mapId != null) {
                tooltipData.add(new MapTooltipComponent(mapId));
            }
        } else {
            drawContainerTooltip(tooltipData, itemStack, item);
        }

        drawBytesTooltip(tooltipData, itemStack);
    }

    private void drawBytesTooltip(List<TooltipData> tooltipData, ItemStack itemStack) {
        try {
            itemStack.writeNbt(new NbtCompound()).write(ByteCountDataOutput.INSTANCE);
            final int byteCount = ByteCountDataOutput.INSTANCE.getCount();
            ByteCountDataOutput.INSTANCE.reset();
            tooltipData.add(new TextTooltipComponent(Text.literal(StringUtils.formatBytes(byteCount))
                    .formatted(Formatting.GRAY).asOrderedText()));
        } catch (IOException e) {
            Vandalism.getInstance().getLogger().error("Failed to write item stack to nbt.", e);
        }
    }

    private void drawCompassTooltip(List<TooltipData> tooltipData, ItemStack itemStack) {
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
        final Text dimension = Text.literal("Dimension: ").formatted(Formatting.GRAY)
                .append(Text.literal(globalPos.getDimension().getValue().toString()).formatted(Formatting.GOLD));

        tooltipData.add(new TextTooltipComponent(position.asOrderedText()));
        tooltipData.add(new TextTooltipComponent(dimension.asOrderedText()));
    }

    private void drawBannerPatternTooltip(List<TooltipData> tooltipData, BannerPatternItem patternItem) {
        final Optional<RegistryEntryList.Named<BannerPattern>> optionalList = Registries.BANNER_PATTERN.getEntryList(patternItem.getPattern());

        if (optionalList.isPresent()) {
            final RegistryEntryList.Named<BannerPattern> list = optionalList.get();
            final RegistryEntry<BannerPattern> bannerPattern = (list.size() > 0 ? list.get(0) : null);

            if (bannerPattern != null) {
                final ItemStack bannerItem = new ItemStack(Items.GRAY_BANNER);

                bannerItem.getOrCreateSubNbt("BlockEntityTag").put("Patterns",
                        new BannerPattern.Patterns().add(BannerPatterns.BASE,
                                DyeColor.BLACK).add(bannerPattern, DyeColor.WHITE).toNbt());
                tooltipData.add(new BannerTooltipComponent(bannerItem));
            }
        }
    }

    private void drawContainerTooltip(List<TooltipData> tooltipData, ItemStack itemStack, Item item) {
        final NbtCompound compoundTag = itemStack.getSubNbt("BlockEntityTag");

        if (compoundTag != null && compoundTag.contains("Items", 9) && item instanceof BlockItem blockItem) {
            final Block block = blockItem.getBlock();
            Color color = Color.WHITE;

            if (block instanceof ShulkerBoxBlock shulkerBoxBlock) {
                final DyeColor dye = shulkerBoxBlock.getColor();

                if (dye != null) {
                    final float[] dyeColor = dye.getColorComponents();

                    if (dyeColor.length == 3) {
                        color = new Color(dyeColor[0], dyeColor[1], dyeColor[2]);
                    }
                }
            }

            final DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
            Inventories.readNbt(compoundTag, itemStacks);

            tooltipData.add(new TextTooltipComponent(Text.literal("(Press alt to open inventory)").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)).asOrderedText()));
            tooltipData.add(new ContainerTooltipComponent(itemStacks, color));
        }
    }

}
