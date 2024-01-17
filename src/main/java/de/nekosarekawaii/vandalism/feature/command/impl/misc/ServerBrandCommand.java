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

package de.nekosarekawaii.vandalism.feature.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import net.minecraft.command.CommandSource;

public class ServerBrandCommand extends AbstractCommand {

    public ServerBrandCommand() {
        super(
                "Lets you view and copy the brand from the server you are currently connected to.",
                Category.MISC,
                "serverbrand",
                "brand"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("view").executes(context -> {
            ChatUtil.infoChatMessage("Server Brand: " + this.mc.getNetworkHandler().getBrand());
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("copy").executes(context -> {
            this.mc.keyboard.setClipboard(this.mc.getNetworkHandler().getBrand());
            ChatUtil.infoChatMessage("Server Brand copied into the Clipboard.");
            return SINGLE_SUCCESS;
        }));
    }

}
