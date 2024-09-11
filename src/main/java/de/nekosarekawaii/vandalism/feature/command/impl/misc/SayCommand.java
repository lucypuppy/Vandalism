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

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.injection.access.IClientPlayNetworkHandler;
import de.nekosarekawaii.vandalism.util.Placeholders;
import net.minecraft.command.CommandSource;

public class SayCommand extends AbstractCommand {

    public SayCommand() {
        super(
                "Allows you to send every message into the chat by skipping the command system of this client.",
                Category.MISC,
                "say"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("message", StringArgumentType.greedyString()).executes(context -> {
            final String message = Placeholders.applyReplacements(StringArgumentType.getString(context, "message"));
            if (message.startsWith("/") && message.length() > 1) {
                this.mc.getNetworkHandler().sendChatCommand(message.substring(1));
            } else {
                ((IClientPlayNetworkHandler) this.mc.getNetworkHandler()).vandalism$sendChatMessage(message);
            }
            return SINGLE_SUCCESS;
        }));
    }

}
