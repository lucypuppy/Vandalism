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
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.InventoryUtil;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class AutoSoupModule extends Module implements PlayerUpdateListener {

    private int lastSlot;
    @Getter
    private State state = State.WAITING;

    public AutoSoupModule() {
        super("Auto Soup", "Automatically heals you by drinking soups in your hotbar.", Category.MISC);
    }

    private final IntegerValue health = new IntegerValue(this, "Health", "The amount of health at which you want to start souping.", 8, 1, 20);

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
        final int soupSlot = getStewSlot();
        switch (state) {
            case WAITING:
                if (mc.player.getHealth() > health.getValue() || soupSlot == -1 || mc.currentScreen != null) {
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
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack != null && stack.getItem() == Items.MUSHROOM_STEW) {
                return i;
            }
        }
        return -1;
    }

    public enum State {
        WAITING,
        SWAP_SOUP,
        SOUPING,
        SWAP_BACK
    }
}
