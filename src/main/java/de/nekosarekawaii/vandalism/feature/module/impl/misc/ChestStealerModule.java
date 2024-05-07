/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.LongValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.common.MSTimer;
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
    private MSTimer cpsTimer;
    private long delay;
    private long cpsDelay;

    private final LongValue startDelay = new LongValue(this, "Start Delay", "Delay before starting to take items.", 0L, 0L, 1000L);
    private final LongValue minDelay = new LongValue(this, "Min Delay", "Min delay between taking items.", 100L, 0L, 1000L);
    private final LongValue maxDelay = new LongValue(this, "Max Delay", "Max delay between taking items.", 100L, 0L, 1000L);
    private final IntegerValue minCPS = new IntegerValue(this, "Min CPS", "Min clicks per second.", 3, 1, 20);
    private final IntegerValue maxCPS = new IntegerValue(this, "Max CPS", "Max clicks per second.", 6, 1, 20);
    private final BooleanValue autoClose = new BooleanValue(this, "Auto Close", "Automatically closes the chest when it's empty.", true);
    private final BooleanValue filterItems = new BooleanValue(this, "Filter Items", "Only take items that are useful.", true);

    public ChestStealerModule() {
        super("Chest Stealer", "Automatically steals items from chests.", Category.MISC);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID);
        timer = new MSTimer();
        startTimer = new MSTimer();
        cpsTimer = new MSTimer();
        updateDelay();
        updateCPS();
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        if (!startTimer.hasReached(startDelay.getValue(), false)) return;

        if (mc.currentScreen instanceof GenericContainerScreen screen) {
            for (int i = 0; i < screen.getScreenHandler().getRows() * 9; i++) {
                final ItemStack itemStack = screen.getScreenHandler().slots.get(i).getStack();
                if (itemStack.isEmpty() || (filterItems.getValue() && !canTakeItem(itemStack)))
                    continue;

                if (cpsTimer.hasReached(cpsDelay, true) && timer.hasReached(delay, true)) {
                    updateCPS();
                    updateDelay();

                    mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
                }
            }

            boolean canClose = true;
            for (int i = 0; i < screen.getScreenHandler().getRows() * 9; i++) {
                ItemStack itemStack = screen.getScreenHandler().slots.get(i).getStack();
                if (itemStack.isEmpty() || (filterItems.getValue() && !canTakeItem(itemStack)))
                    continue;

                canClose = false;
            }

            if (canClose && autoClose.getValue()) {
                mc.player.closeHandledScreen();
            }
        } else {
            startTimer.reset();
        }
    }

    private void updateDelay() {
        this.delay = (int) (ThreadLocalRandom.current().nextGaussian() * (minDelay.getValue() - maxDelay.getValue())) + maxDelay.getValue();
    }

    private void updateCPS() {
        this.cpsDelay = this.delay = (int) (ThreadLocalRandom.current().nextGaussian() * (1000 / (minCPS.getValue() + 2) - 1000 / (maxCPS.getValue() + 2) + 1)) + 1000 / (maxCPS.getValue() + 2);
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