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

package de.nekosarekawaii.vandalism.feature.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.Command;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

public class InventoryClearCommand extends Command {

    public InventoryClearCommand() {
        super("Clears your inventory.", Category.MISC, "inventoryclear", "clearinventory", "invclear", "clearinv");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            final DefaultedList<ItemStack> mainInventory = this.mc.player.getInventory().main;
            for (int i = 0; i < mainInventory.size(); ++i) {
                if (mainInventory.get(i).isEmpty()) continue;
                this.clearSlot(i);
            }

            for (int i = 36; i < 46; i++) this.clearSlot(i);
            ChatUtil.infoChatMessage("Your inventory has been cleared.");
            return SINGLE_SUCCESS;
        });
    }

    private void clearSlot(final int id) {
        switch (this.mc.interactionManager.getCurrentGameMode()) {
            case CREATIVE ->
                    this.mc.getNetworkHandler().sendPacket(new CreativeInventoryActionC2SPacket(
                            id,
                            ItemStack.EMPTY
                    ));
            case SURVIVAL, ADVENTURE ->
                    this.mc.interactionManager.clickSlot(
                            this.mc.player.currentScreenHandler.syncId,
                            id,
                            -999,
                            SlotActionType.THROW,
                            this.mc.player
                    );
            default -> {}
        }
    }

}
