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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.Command;
import de.nekosarekawaii.vandalism.util.*;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;

public class UsernameCommand extends Command {

    public UsernameCommand() {
        super(
                "Allows you to view, copy and change your username.",
                Category.MISC,
                "username",
                "name",
                "ign"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            ChatUtil.infoChatMessage("Your name is: " + Formatting.DARK_AQUA + this.mc.session.getUsername());
            return SINGLE_SUCCESS;
        });
        builder.then(literal("copy").executes(context -> {
            this.mc.keyboard.setClipboard(this.mc.session.getUsername());
            ChatUtil.infoChatMessage("Name copied into the clipboard.");
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("change").then(argument("name", StringArgumentType.word()).executes(context -> {
            this.login(StringArgumentType.getString(context, "name"));
            return SINGLE_SUCCESS;
        })));
        builder.then(literal("change-and-reconnect").then(argument("name", StringArgumentType.word()).executes(context -> {
            this.login(StringArgumentType.getString(context, "name"));
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                } catch (final InterruptedException ignored) {
                }
                RenderSystem.recordRenderCall(ServerUtil::connectToLastServer);
            }).start();
            return SINGLE_SUCCESS;
        })));
    }

    private void login(final String name) {
        if (!MathUtil.isBetween(name.length(), MinecraftConstants.MIN_USERNAME_LENGTH, MinecraftConstants.MAX_USERNAME_LENGTH)) {
            ChatUtil.errorChatMessage("The name must be between " + MinecraftConstants.MIN_USERNAME_LENGTH + " and " + MinecraftConstants.MAX_USERNAME_LENGTH + " characters long.");
            return;
        }
        SessionUtil.setSessionAsync(name, "");
        ChatUtil.infoChatMessage("Username changed to: " + Formatting.DARK_AQUA + name);
    }

}
