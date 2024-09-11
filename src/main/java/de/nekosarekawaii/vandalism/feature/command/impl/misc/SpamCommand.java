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

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.injection.access.IClientPlayNetworkHandler;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import de.nekosarekawaii.vandalism.util.Placeholders;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;

public class SpamCommand extends AbstractCommand {

    private static boolean IS_RUNNING = false;

    public SpamCommand() {
        super(
                "Allows you to spam in the chat.",
                Category.MISC,
                "spam"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("times", IntegerArgumentType.integer(2, 2000)).
                then(argument("delay", IntegerArgumentType.integer(0, 10000)).
                        then(argument("message", StringArgumentType.greedyString()).executes(context -> {
                                    if (IS_RUNNING) {
                                        ChatUtil.errorChatMessage("Already spamming!");
                                        return SINGLE_SUCCESS;
                                    }
                                    final int times = IntegerArgumentType.getInteger(context, "times");
                                    final int delay = IntegerArgumentType.getInteger(context, "delay");
                                    final String message = StringArgumentType.getString(context, "message");
                            new Thread(() -> {
                                        IS_RUNNING = true;
                                        for (int i = 0; i < times; i++) {
                                            final ClientPlayNetworkHandler networkHandler = this.mc.getNetworkHandler();
                                            if (networkHandler == null) break;
                                            if (message.startsWith("/") && message.length() > 1) {
                                                this.mc.getNetworkHandler().sendChatCommand(Placeholders.applyReplacements(message.substring(1)));
                                            } else {
                                                ((IClientPlayNetworkHandler) this.mc.getNetworkHandler()).vandalism$sendChatMessage(Placeholders.applyReplacements(message));
                                            }
                                            try {
                                                Thread.sleep(delay);
                                            } catch (InterruptedException ignored) {
                                            }
                                        }
                                        IS_RUNNING = false;
                            }).start();
                                    return SINGLE_SUCCESS;
                                })
                        ))
        );
    }

}
