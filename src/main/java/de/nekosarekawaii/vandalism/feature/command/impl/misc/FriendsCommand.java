/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.command.Command;
import de.nekosarekawaii.vandalism.feature.command.arguments.FriendArgumentType;
import de.nekosarekawaii.vandalism.feature.command.arguments.PlayerArgumentType;
import de.nekosarekawaii.vandalism.integration.friends.Friend;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import net.minecraft.command.CommandSource;

public class FriendsCommand extends Command {

    public FriendsCommand() {
        super("Manage your client friends.", Category.MISC, "friends", "friend");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("add").then(argument("name", PlayerArgumentType.create()).executes(context -> {
            final String name = PlayerArgumentType.get(context);
            Vandalism.getInstance().getFriendsManager().addFriend(name, name);
            return SINGLE_SUCCESS;
        }).then(argument("alias", StringArgumentType.word()).executes(context -> {
            Vandalism.getInstance().getFriendsManager().addFriend(PlayerArgumentType.get(context), StringArgumentType.getString(context, "alias"));
            return SINGLE_SUCCESS;
        })))).then(literal("remove").then(argument("friend", FriendArgumentType.create()).executes(context -> {
            Vandalism.getInstance().getFriendsManager().removeFriend(FriendArgumentType.get(context));
            return SINGLE_SUCCESS;
        }))).then(literal("list").executes(context -> {
            if (Vandalism.getInstance().getFriendsManager().getList().isEmpty()) {
                ChatUtil.infoChatMessage("You don't have any friends :p");
                return SINGLE_SUCCESS;
            }
            ChatUtil.infoChatMessage("Your friends: " + String.join(", ", Vandalism.getInstance().getFriendsManager().getList().stream().map(friend -> friend.getName() + (!friend.getName().equals(friend.getAlias()) ? " (" + friend.getAlias() + ")" : "")).toList()));
            return SINGLE_SUCCESS;
        })).then(literal("clear").executes(context -> {
            final int amount = Vandalism.getInstance().getFriendsManager().getList().size();
            Vandalism.getInstance().getFriendsManager().getList().clear();
            ChatUtil.infoChatMessage("Cleared " + amount + " friend/s.");
            return SINGLE_SUCCESS;
        })).then(literal("alias").then(literal("set").then(argument("friend", FriendArgumentType.create()).then(argument("alias", StringArgumentType.word()).executes(context -> {
            final Friend friend = FriendArgumentType.get(context);
            final String alias = StringArgumentType.getString(context, "alias");
            friend.setAlias(alias);
            ChatUtil.infoChatMessage("Set " + friend.getName() + "'s alias to " + alias + ".");
            return SINGLE_SUCCESS;
        })))).then(literal("remove").then(argument("friend", FriendArgumentType.create()).executes(context -> {
            final Friend friend = FriendArgumentType.get(context);
            friend.setAlias(friend.getName());
            ChatUtil.infoChatMessage("Removed " + friend.getName() + "'s alias.");
            return SINGLE_SUCCESS;
        }))));
    }

}
