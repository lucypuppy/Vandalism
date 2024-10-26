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

import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.Command;
import de.nekosarekawaii.vandalism.util.*;
import de.nekosarekawaii.vandalism.util.math.MathUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Optional;
import java.util.UUID;

public class SkullCommand extends Command {

    public SkullCommand() {
        super("Gives you a player head.", Category.EXPLOIT, "skull", "head");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("name", StringArgumentType.word()).executes(context -> {
            final String username = StringArgumentType.getString(context, "name");
            if (!MathUtil.isBetween(username.length(), MinecraftConstants.MIN_USERNAME_LENGTH, MinecraftConstants.MAX_USERNAME_LENGTH)) {
                ChatUtil.errorChatMessage("The name must be between " + MinecraftConstants.MIN_USERNAME_LENGTH + " and " + MinecraftConstants.MAX_USERNAME_LENGTH + " characters long.");
                return SINGLE_SUCCESS;
            }
            new Thread(() -> {
                final ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD);
                final UUID uuid;
                try {
                    uuid = UUID.fromString(UUIDUtil.getUUIDFromName(username));
                } catch (final Exception ignored) {
                    ChatUtil.errorChatMessage("An error occurred while trying to get the UUID of the player.");
                    return;
                }
                final ProfileResult profileResult = this.mc.sessionService.fetchProfile(uuid, true);
                if (profileResult != null) {
                    itemStack.set(DataComponentTypes.PROFILE, new ProfileComponent(Optional.of(username), Optional.of(uuid), profileResult.profile().getProperties()));
                    ItemStackUtil.giveItemStack(itemStack, true);
                } else {
                    ChatUtil.errorChatMessage("An error occurred while trying to get the player data.");
                }
            }).start();
            return SINGLE_SUCCESS;
        }));
    }

}
