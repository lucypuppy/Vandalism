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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.LongValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.InventoryUtil;
import de.nekosarekawaii.vandalism.util.MSTimer;
import net.minecraft.block.FallingBlock;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ChestStealerModule extends AbstractModule implements PlayerUpdateListener {

    private MSTimer timer;
    private MSTimer startTimer;
    private MSTimer closeTimer;
    private long delay;

    private final LongValue startDelay = new LongValue(this, "Start Delay", "Delay before starting to take items.", 0L, 0L, 1000L);
    private final LongValue minDelay = new LongValue(this, "Min Delay", "Min delay between taking items.", 100L, 0L, 1000L);
    private final LongValue maxDelay = new LongValue(this, "Max Delay", "Max delay between taking items.", 100L, 0L, 1000L);

    private final BooleanValue autoClose = new BooleanValue(this, "Auto Close", "Automatically closes the chest when it's empty.", true);
    private final LongValue closeDelay = new LongValue(this, "Close Delay", "Delay before closing the chest.", 0L, 0L, 1000L).visibleCondition(autoClose::getValue);

    private final BooleanValue filterItems = new BooleanValue(this, "Filter Items", "Only take items that are useful.", true);

    public ChestStealerModule() {
        super("Chest Stealer", "Automatically steals items from chests.", Category.MISC);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID);
        timer = new MSTimer();
        startTimer = new MSTimer();
        closeTimer = new MSTimer();
        updateDelay();
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        if (!startTimer.hasReached(startDelay.getValue(), false))
            return;

        if (mc.currentScreen instanceof GenericContainerScreen screen) {
            boolean canClose = true;

            // This swaps items directly into the hotbar.
            for (int i = 0; i < screen.getScreenHandler().slots.size() - 36; i++) {
                final ItemStack itemStack = screen.getScreenHandler().slots.get(i).getStack();

                if (!itemStack.isEmpty()) {
                    final int slot = InventoryUtil.getHotbarSlotForItem(itemStack);

                    if (slot == -1) //Invalid Item
                        continue;

                    final ItemStack hotbarStack = screen.getScreenHandler().slots.get(slot + 54).getStack();
                    if (hotbarStack.getItem() instanceof AirBlockItem || InventoryUtil.isItemBetter(itemStack, hotbarStack)) {
                        canClose = false;
                        stealItems(screen, i, slot, SlotActionType.SWAP);
                    }
                }
            }

            // This grabs stuff like armor, More blocks etc.
            for (int i = 0; i < screen.getScreenHandler().getRows() * 9; i++) {
                final ItemStack itemStack = screen.getScreenHandler().slots.get(i).getStack();
                final int slot = InventoryUtil.getHotbarSlotForItem(itemStack);

                if (slot != -1 && slot != 8) // Invalid Item
                    continue;

                if (itemStack.isEmpty() || (filterItems.getValue() && !canTakeItem(itemStack)))
                    continue;

                canClose = false;
                stealItems(screen, i, 0, SlotActionType.QUICK_MOVE);
            }

            if (canClose) {
                if (autoClose.getValue() && closeTimer.hasReached(closeDelay.getValue(), true)) {
                    mc.player.closeHandledScreen();
                }
            } else {
                closeTimer.reset();
            }
        } else {
            startTimer.reset();
        }
    }

    private void updateDelay() {
        this.delay = (int) (ThreadLocalRandom.current().nextGaussian() * (minDelay.getValue() - maxDelay.getValue())) + maxDelay.getValue();
    }

    private void stealItems(final GenericContainerScreen screen, int slotId, int slot, SlotActionType actionType) {
        if (timer.hasReached(delay, true)) {
            updateDelay();
            mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, slotId, slot, actionType, mc.player);
        }
    }

    private boolean canTakeItem(ItemStack itemStack) {
        Item item = itemStack.getItem();

        final ArrayList<Item> whitelistedItems = new ArrayList<>() {
            {
                add(Items.FISHING_ROD);
                add(Items.BOW);
                add(Items.ARROW);
                add(Items.TOTEM_OF_UNDYING);
                add(Items.ENDER_PEARL);
                add(Items.SNOWBALL);
                add(Items.EGG);
                add(Items.DIAMOND);
                add(Items.EMERALD);
                add(Items.GOLD_INGOT);
                add(Items.IRON_INGOT);
                add(Items.NETHERITE_INGOT);
                add(Items.REDSTONE);
                add(Items.LAPIS_LAZULI);
                add(Items.COAL);
                add(Items.QUARTZ);
                add(Items.WATER_BUCKET);
                add(Items.LAVA_BUCKET);
            }
        };


        return whitelistedItems.contains(item) ||
                item instanceof ToolItem ||
                item instanceof ArmorItem ||
                (item instanceof BlockItem &&
                        (item != Items.SLIME_BLOCK &&
                                item != Items.TNT &&
                                item != Items.SOUL_SAND &&
                                !(((BlockItem) item).getBlock() instanceof FallingBlock))) ||
                item.getComponents().contains(DataComponentTypes.FOOD);
    }

}