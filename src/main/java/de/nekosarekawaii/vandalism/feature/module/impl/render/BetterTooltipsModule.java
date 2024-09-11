/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.misc.KeyBindValue;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.event.render.TooltipDrawListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.ByteCountDataOutput;
import de.nekosarekawaii.vandalism.util.StringUtils;
import de.nekosarekawaii.vandalism.util.tooltip.CustomContainerScreen;
import de.nekosarekawaii.vandalism.util.tooltip.impl.BannerTooltipComponent;
import de.nekosarekawaii.vandalism.util.tooltip.impl.MapTooltipComponent;
import de.nekosarekawaii.vandalism.util.tooltip.impl.TextTooltipComponent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.GlobalPos;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class BetterTooltipsModule extends AbstractModule implements TooltipDrawListener, OutgoingPacketListener {

    private final KeyBindValue openContainerKey = new KeyBindValue(
            this,
            "Open Container Key",
            "Allows you to open a container item you are currently hovering over by pressing the key.",
            GLFW.GLFW_KEY_LEFT_ALT,
            false
    );

    public BetterTooltipsModule() {
        super("Better Tooltips", "Improves item tooltips from the game.", Category.RENDER);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(TooltipDrawEvent.ID, this);
        Vandalism.getInstance().getEventSystem().subscribe(OutgoingPacketEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(TooltipDrawEvent.ID, this);
        Vandalism.getInstance().getEventSystem().unsubscribe(OutgoingPacketEvent.ID, this);
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        if (mc.currentScreen instanceof CustomContainerScreen) {
            final Packet<?> packet = event.packet;
            if (packet instanceof CloseHandledScreenC2SPacket) {
                event.cancel();
            }
        }
    }

    @Override
    public void onTooltipDraw(final TooltipDrawEvent event) {
        final List<TooltipData> tooltipData = event.tooltipData;
        final ItemStack itemStack = event.itemStack;
        final Item item = itemStack.getItem();
        switch (item) {
            case CompassItem stack when stack.getComponents().contains(DataComponentTypes.LODESTONE_TRACKER) ->
                    drawCompassTooltip(tooltipData, itemStack);
            case BannerPatternItem patternItem -> drawBannerPatternTooltip(tooltipData, patternItem);
            case BannerItem bannerItem -> tooltipData.add(new BannerTooltipComponent(itemStack));
            case FilledMapItem stack -> {
                final MapIdComponent mapId = stack.getComponents().get(DataComponentTypes.MAP_ID);
                if (mapId != null) {
                    tooltipData.add(new MapTooltipComponent(mapId));
                }
            }
            case null, default -> drawContainerTooltip(tooltipData, itemStack, item);
        }
        drawBytesTooltip(tooltipData, itemStack);
    }

    private void drawBytesTooltip(final List<TooltipData> tooltipData, final ItemStack itemStack) {
        try {
            itemStack.encode(mc.player.getRegistryManager()).write(ByteCountDataOutput.INSTANCE);
            final int byteCount = ByteCountDataOutput.INSTANCE.getCount();
            ByteCountDataOutput.INSTANCE.reset();
            tooltipData.add(new TextTooltipComponent(Text.literal(StringUtils.formatBytes(byteCount)).formatted(Formatting.GRAY).asOrderedText()));
        } catch (final Exception ignored) {
            tooltipData.add(new TextTooltipComponent(Text.literal("Error getting bytes.").formatted(Formatting.RED).asOrderedText()));
        }

    }

    private void drawCompassTooltip(final List<TooltipData> tooltipData, final ItemStack itemStack) {
        final LodestoneTrackerComponent lodestoneComponent = itemStack.getComponents().get(DataComponentTypes.LODESTONE_TRACKER);
        if (lodestoneComponent == null || lodestoneComponent.target().isEmpty()) {
            return;
        }

        final GlobalPos globalPos = lodestoneComponent.target().get();
        final Text posText = Text.literal(String.format("X: %d, Y: %d, Z: %d", globalPos.pos().getX(), globalPos.pos().getY(), globalPos.pos().getZ())).formatted(Formatting.GOLD);
        final Text position = Text.literal("Position: ").formatted(Formatting.GRAY).append(posText);
        final Text dimension = Text.literal("Dimension: ").formatted(Formatting.GRAY).append(
                Text.literal(
                        globalPos.dimension().getValue().toString()
                ).formatted(Formatting.GOLD)
        );

        tooltipData.add(new TextTooltipComponent(position.asOrderedText()));
        tooltipData.add(new TextTooltipComponent(dimension.asOrderedText()));
    }

    // TODO: Fix
    private void drawBannerPatternTooltip(final List<TooltipData> tooltipData, final BannerPatternItem patternItem) {
    /*    final Optional<RegistryEntryList.Named<BannerPattern>> optionalList = Registries.BANNER_PATTERN.getEntryList(patternItem.getPattern());

        if (optionalList.isPresent()) {
            final RegistryEntryList.Named<BannerPattern> list = optionalList.get();
            final RegistryEntry<BannerPattern> bannerPattern = (list.size() > 0 ? list.get(0) : null);

            if (bannerPattern != null) {
                final ItemStack bannerItem = new ItemStack(Items.GRAY_BANNER);

               bannerItem.getOrCreateSubNbt("BlockEntityTag").put("Patterns",
                        new BannerPattern.Patterns().add(
                                BannerPatterns.BASE,
                                DyeColor.BLACK
                        ).add(
                                bannerPattern,
                                DyeColor.WHITE
                        ).toNbt()
                );
                tooltipData.add(new BannerTooltipComponent(bannerItem));
            }
        }*/
    }

    // TODO: Fix
    private void drawContainerTooltip(final List<TooltipData> tooltipData, final ItemStack itemStack, final Item item) {
        /*final NbtCompound compoundTag = itemStack.getSubNbt("BlockEntityTag");
        if (compoundTag != null && item instanceof BlockItem blockItem) {
            final boolean isGenericContainer = compoundTag.contains("Items", 9);
            if (isGenericContainer || compoundTag.contains("RecordItem")) {
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
                if (isGenericContainer) {
                    Inventories.readNbt(compoundTag, itemStacks);
                }
                else {
                    itemStacks.set(0, ItemStack.fromNbt(compoundTag.getCompound("RecordItem")));
                }
                if (!itemStacks.isEmpty()) {
                    tooltipData.add(new TextTooltipComponent(
                            Text.literal(
                                    "(Press " + InputType.getName(this.openContainerKey.getValue()) + " to open this inventory)"
                            ).setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)).asOrderedText()
                    ));
                    tooltipData.add(new ContainerTooltipComponent(itemStacks, color));
                    if (this.openContainerKey.isPressed()) {
                        final GenericContainerScreenHandler genericContainerScreen = GenericContainerScreenHandler.createGeneric9x3(
                                0,
                                this.mc.player.getInventory(),
                                new InventoryNoop(itemStacks)
                        );
                        this.mc.setScreen(new CustomContainerScreen(
                                genericContainerScreen,
                                this.mc.player.getInventory(),
                                Text.of(itemStack.getName())
                        ));
                    }
                }
            }
        }*/
    }

}
