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
import de.nekosarekawaii.vandalism.feature.command.arguments.GameModeArgumentType;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

public class ClientsideGameModeCommand extends AbstractCommand {

    public ClientsideGameModeCommand() {
        super("Allows you to set your clientside game mode.", Category.RENDER, "clientsidegamemode", "fakegamemode", "cgm", "fgm");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("gamemode", GameModeArgumentType.create()).executes(context -> {
            if (this.mc.interactionManager != null) {
                final GameMode gameMode = GameModeArgumentType.get(context);
                this.mc.interactionManager.setGameMode(gameMode);
                ChatUtil.infoChatMessage(Formatting.GREEN + "Your Clientside Game Mode has been set to" + Formatting.DARK_GRAY + ": " + Formatting.GOLD + gameMode.name() + " " + Formatting.DARK_GRAY + "(" + Formatting.DARK_AQUA + gameMode.getId() + Formatting.DARK_GRAY + ")");
            }

            return SINGLE_SUCCESS;
        }));
    }

}
