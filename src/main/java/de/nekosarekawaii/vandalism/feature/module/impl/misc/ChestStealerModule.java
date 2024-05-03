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
import de.nekosarekawaii.vandalism.base.value.impl.number.LongValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.common.MSTimer;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.screen.slot.SlotActionType;

import java.util.concurrent.ThreadLocalRandom;

public class ChestStealerModule extends AbstractModule implements PlayerUpdateListener {

    private MSTimer timer;
    private MSTimer startTimer;
    private long delay;

    private final LongValue startDelay = new LongValue(this, "Start Delay", "Delay before starting to take items.", 0L, 0L, 1000L);
    private final LongValue minDelay = new LongValue(this, "Min Delay", "Min delay between taking items.", 100L, 0L, 1000L);
    private final LongValue maxDelay = new LongValue(this, "Max Delay", "Max delay between taking items.", 100L, 0L, 1000L);
    private final BooleanValue autoClose = new BooleanValue(this, "Auto Close", "Automatically closes the chest when it's empty.", true);

    public ChestStealerModule() {
        super("Chest Stealer", "Automatically steals items from chests.", Category.MISC);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID);
        timer = new MSTimer();
        startTimer = new MSTimer();
        updateDelay();
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        if (!startTimer.hasReached(startDelay.getValue(), false)) return;

        if (mc.currentScreen instanceof final GenericContainerScreen screen) {
            for (int i = 0; i < screen.getScreenHandler().getRows() * 9; i++) {
                if (screen.getScreenHandler().slots.get(i).getStack().isEmpty()) continue;
                if (timer.hasReached(delay, true)) {
                    updateDelay();
                    mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
                }
            }

            if (screen.getScreenHandler().getInventory().isEmpty()) {
                if (autoClose.getValue()) {
                    mc.player.closeHandledScreen();
                }
            }
        } else {
            startTimer.reset();
        }
    }

    private void updateDelay() {
        this.delay = (int) (ThreadLocalRandom.current().nextGaussian() * (minDelay.getValue() - maxDelay.getValue())) + maxDelay.getValue();
    }

//    private boolean canTakeItem(Item item) {
//        if(item instanceof final SwordItem swordItem) {
//            for()
//        }
//    }
}