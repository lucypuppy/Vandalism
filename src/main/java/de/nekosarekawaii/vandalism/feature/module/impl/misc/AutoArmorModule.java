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
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.LongValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.InventoryUtil;
import de.nekosarekawaii.vandalism.util.MSTimer;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

import java.util.concurrent.ThreadLocalRandom;

public class AutoArmorModule extends Module implements PlayerUpdateListener {

    private MSTimer timer;
    private MSTimer startTimer;
    private MSTimer cpsTimer;
    private long delay;
    private long cpsDelay;

    public AutoArmorModule() {
        super("Auto Armor", "Automatically equips the best armor in your inventory.", Category.MISC);
    }

    private final LongValue startDelay = new LongValue(this, "Start Delay", "Delay before starting to take items.", 0L, 0L, 1000L);
    private final LongValue minDelay = new LongValue(this, "Min Delay", "Min delay between taking items.", 100L, 0L, 1000L);
    private final LongValue maxDelay = new LongValue(this, "Max Delay", "Max delay between taking items.", 100L, 0L, 1000L);
    private final IntegerValue minCPS = new IntegerValue(this, "Min CPS", "Min clicks per second.", 3, 1, 20);
    private final IntegerValue maxCPS = new IntegerValue(this, "Max CPS", "Max clicks per second.", 6, 1, 20);

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
        if (mc.currentScreen instanceof InventoryScreen screen) {
            for (int i = 5; i <= 8; i++) {
                ItemStack stack = screen.getScreenHandler().slots.get(i).getStack();
                if (stack.isEmpty() || InventoryUtil.isBestArmor(stack)) {
                    continue;
                }
                if (cpsTimer.hasReached(cpsDelay, true) && timer.hasReached(delay, true)) {
                    updateCPS();
                    updateDelay();
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 1, SlotActionType.THROW, mc.player);
                }
            }
            for(int i = 9; i <= 44; i++) {
                ItemStack stack = screen.getScreenHandler().slots.get(i).getStack();
                if (stack.isEmpty() || !(stack.getItem() instanceof ArmorItem) || !InventoryUtil.isBestArmor(stack)) {
                    continue;
                }

                ArmorItem armorItem = (ArmorItem) stack.getItem();
                if (!screen.getScreenHandler().slots.get(5 + armorItem.getType().ordinal()).getStack().isEmpty())
                    continue;

                if (cpsTimer.hasReached(cpsDelay, true) && timer.hasReached(delay, true)) {
                    updateCPS();
                    updateDelay();
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
                }
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
}
