/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.MultiModeValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.InventoryUtil;
import de.nekosarekawaii.vandalism.util.math.RandomUtils;
import lombok.Getter;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoSoupModule extends Module implements PlayerUpdateListener {

    private int lastSlot;
    private int refillTicks, refillDelay;
    private int openInvTicks, openInvDelay;
    @Getter
    private State state = State.WAITING;

    public AutoSoupModule() {
        super("Auto Soup", "Automatically heals you by drinking soups in your hotbar.", Category.MISC);
    }

    /*
     * Soup Settings
     */
    private final IntegerValue minHealth = new IntegerValue(this, "Min Health", "The minimum amount of health at which you want to start souping.", 8, 1, 20);
    private final IntegerValue maxHealth = new IntegerValue(this, "Max Health", "The maximum amount of health at which you want to start souping.", 10, 1, 20);

    /*
     * Refill Settings
     */
    private final BooleanValue autoRefill = new BooleanValue(this, "Auto Refill", "Automatically refills the soup slots.", true);
    private final ValueGroup refillGroup = new ValueGroup(this, "Refill Settings", "Settings for the auto refill feature.").visibleCondition(autoRefill::getValue);
    private final IntegerValue minRefillTicks = new IntegerValue(refillGroup, "Min Refill Ticks", "The minimum delay between refilling slots.", 1, 0, 10);
    private final IntegerValue maxRefillTicks = new IntegerValue(refillGroup, "Max Refill Ticks", "The maximum delay between refilling slots.", 2, 0, 10);
    private final IntegerValue minOpenInvTicks = new IntegerValue(refillGroup, "Min Open Inventory Ticks", "The minimum delay between opening the inventory and souping.", 1, 1, 10);
    private final IntegerValue maxOpenInvTicks = new IntegerValue(refillGroup, "Max Open Inventory Ticks", "The maximum delay between opening the inventory and souping.", 2, 1, 10);
    private final MultiModeValue refillSlots = new MultiModeValue(refillGroup, "Refill Slots", "The slots you want to refill.", "1", "2", "3", "4", "5", "6", "7", "8", "9");

    @Override
    protected void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID);
        this.state = State.WAITING;
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        updateDelay();
        if (mc.currentScreen instanceof InventoryScreen && autoRefill.getValue()) {
            moveStews();
            openInvTicks++;
            refillTicks++;
            return;
        } else {
            openInvTicks = 0;
            refillTicks = 0;
        }
        final int soupSlot = getStewSlot();
        switch (state) {
            case WAITING:
                if (mc.player.getHealth() > RandomUtils.randomInt(minHealth.getValue(), maxHealth.getValue()) || soupSlot == -1 || mc.currentScreen != null) {
                    break;
                }
                state = State.SWAP_SOUP;
//                break;
            case SWAP_SOUP:
                lastSlot = mc.player.getInventory().selectedSlot;
                InventoryUtil.setSlot(soupSlot);
                state = State.SOUPING;
                break;
            case SOUPING:
                mc.doItemUse();
                state = State.SWAP_BACK;
                break;
            case SWAP_BACK:
                mc.player.dropSelectedItem(true);
                InventoryUtil.setSlot(lastSlot);
                state = State.WAITING;
                break;
        }
    }

    private int getStewSlot() {
        for (int i = 0; i < 9; i++) {
            final ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack != null && stack.getItem() == Items.MUSHROOM_STEW) {
                return i;
            }
        }
        return -1;
    }

    private void moveStews() {
        final int[] refillSlots = getRefillSlots();
        for (int slot : refillSlots) {
            updateDelay();
            slot -= 1;
            final int invSlot = getInvStewSlot();
            if (invSlot == -1 || openInvTicks < openInvDelay) {
                return;
            }
            if (mc.player.getInventory().getStack(slot).getItem() != Items.AIR && mc.player.getInventory().getStack(slot).getItem() != Items.BOWL) {
                continue;
            }
            if (refillTicks >= refillDelay) {
                refillTicks = 0;
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, invSlot, 0, SlotActionType.QUICK_MOVE, mc.player);
            }
        }
    }

    private int[] getRefillSlots() {
        return refillSlots.getValue().stream().mapToInt(Integer::parseInt).toArray();
    }

    private int getInvStewSlot() {
        for (int i = 9; i < 36; i++) {
            final ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack != null && stack.getItem() == Items.MUSHROOM_STEW) {
                return i;
            }
        }
        return -1;
    }

    private void updateDelay() {
        this.refillDelay = RandomUtils.randomInt(minRefillTicks.getValue(), maxRefillTicks.getValue());
        this.openInvDelay = RandomUtils.randomInt(minOpenInvTicks.getValue(), maxOpenInvTicks.getValue());
    }

    public enum State {
        WAITING,
        SWAP_SOUP,
        SOUPING,
        SWAP_BACK
    }
}
