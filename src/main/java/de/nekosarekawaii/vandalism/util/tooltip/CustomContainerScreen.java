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

package de.nekosarekawaii.vandalism.util.tooltip;

import de.nekosarekawaii.vandalism.util.ItemStackUtil;
import de.nekosarekawaii.vandalism.util.MinecraftConstants;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class CustomContainerScreen extends GenericContainerScreen {

    public CustomContainerScreen(final GenericContainerScreenHandler handler, final PlayerInventory inventory, final Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void onMouseClick(final Slot slot, final int slotId, final int button, final SlotActionType actionType) {
        if (slot == null) return;
        final ItemStack stack = slot.getStack();
        if (stack == null || stack.isEmpty() || stack.getComponents().isEmpty()) return;
        ItemStackUtil.giveItemStack(slot.getStack(), false, MinecraftConstants.LAST_SLOT_IN_HOTBAR);
    }

}
