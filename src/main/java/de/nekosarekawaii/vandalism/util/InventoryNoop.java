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

package de.nekosarekawaii.vandalism.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class InventoryNoop implements Inventory {

    private final List<ItemStack> itemStacks;

    public InventoryNoop(final List<ItemStack> itemStacks) {
        this.itemStacks = itemStacks;
    }

    @Override
    public void clear() {
    }

    @Override
    public int size() {
        return itemStacks.size();
    }

    @Override
    public boolean isEmpty() {
        return itemStacks.isEmpty();
    }

    @Override
    public ItemStack getStack(final int slot) {
        return itemStacks.get(slot).copy();
    }

    @Override
    public ItemStack removeStack(final int slot, final int amount) {
        return null;
    }

    @Override
    public ItemStack removeStack(final int slot) {
        return null;
    }

    @Override
    public void setStack(final int slot, final ItemStack stack) {
    }

    @Override
    public int getMaxCountPerStack() {
        return 128;
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(final PlayerEntity player) {
        return false;
    }

    @Override
    public void onOpen(final PlayerEntity player) {
    }

    @Override
    public void onClose(final PlayerEntity player) {
    }

    @Override
    public boolean isValid(final int slot, final ItemStack stack) {
        return false;
    }

    @Override
    public boolean canTransferTo(final Inventory hopperInventory, final int slot, final ItemStack stack) {
        return false;
    }

    @Override
    public int count(final Item item) {
        return Inventory.super.count(item);
    }

    @Override
    public boolean containsAny(final Set<Item> items) {
        return Inventory.super.containsAny(items);
    }

    @Override
    public boolean containsAny(final Predicate<ItemStack> predicate) {
        return Inventory.super.containsAny(predicate);
    }

}
