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

package de.nekosarekawaii.vandalism.feature.command.impl.misc.copy;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import net.minecraft.command.CommandSource;

public class CopyUsernameCommand extends AbstractCommand {

    public CopyUsernameCommand() {
        super(
                "Copies your username into your clipboard.",
                Category.MISC,
                "copyusername",
                "usernamecopy",
                "copyname",
                "namecopy",
                "copyign",
                "igncopy"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            this.mc.keyboard.setClipboard(this.mc.player.getGameProfile().getName());
            ChatUtil.infoChatMessage("Username copied into the clipboard.");
            return SINGLE_SUCCESS;
        });
    }

}
