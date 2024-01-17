/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.feature.command.impl.render;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;

public class ClientsideInventoryClearCommand extends AbstractCommand {

    public ClientsideInventoryClearCommand() {
        super(
                "Clears your Clientside Inventory.",
                Category.RENDER,
                "clientsideinventoryclear",
                "clientsideinvclear",
                "fakeinventoryclear",
                "fakeinvclear"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            if (this.mc.player.isCreative()) {
                ChatUtil.errorChatMessage(Formatting.RED + "You can't clear your Clientside Inventory in Creative Mode.");
                return SINGLE_SUCCESS;
            }

            this.mc.player.getInventory().clear();
            ChatUtil.infoChatMessage(Formatting.GREEN + "Your Clientside Inventory has been cleared.");
            return SINGLE_SUCCESS;
        });
    }

}
