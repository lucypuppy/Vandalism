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

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.feature.command.arguments.FriendArgumentType;
import de.nekosarekawaii.vandalism.feature.command.arguments.PlayerArgumentType;
import de.nekosarekawaii.vandalism.integration.friends.Friend;
import de.nekosarekawaii.vandalism.integration.friends.FriendManager;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import net.minecraft.command.CommandSource;

public class FriendCommand extends AbstractCommand {

    public FriendCommand() {
        super("Manage your client friends.", Category.MISC, "friend", "friends");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("add").then(argument("name", PlayerArgumentType.create()).executes(context -> {
            final String name = PlayerArgumentType.get(context);
            this.addFriend(name, name);
            return SINGLE_SUCCESS;
        }).then(argument("alias", StringArgumentType.word()).executes(context -> {
            final String name = PlayerArgumentType.get(context);
            final String alias = StringArgumentType.getString(context, "alias");
            this.addFriend(name, alias);
            return SINGLE_SUCCESS;
        })))).then(literal("remove").then(argument("friend", FriendArgumentType.create()).executes(context -> {
            final Friend friend = FriendArgumentType.get(context);
            Vandalism.getInstance().getFriendManager().remove(friend);
            ChatUtil.infoChatMessage("Removed " + friend.getName() + " as a friend.");
            return SINGLE_SUCCESS;
        }))).then(literal("list").executes(context -> {
            if (Vandalism.getInstance().getFriendManager().getList().isEmpty()) {
                ChatUtil.infoChatMessage("You don't have any friends :p");
                return SINGLE_SUCCESS;
            }
            ChatUtil.infoChatMessage("Your friends: " + String.join(", ", Vandalism.getInstance().getFriendManager().getList().stream().map(friend -> friend.getName() + (!friend.getName().equals(friend.getAlias()) ? " (" + friend.getAlias() + ")" : "")).toList()));
            return SINGLE_SUCCESS;
        })).then(literal("clear").executes(context -> {
            int amount = Vandalism.getInstance().getFriendManager().getList().size();
            Vandalism.getInstance().getFriendManager().getList().clear();
            ChatUtil.infoChatMessage("Cleared " + amount + " friends.");
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

    private void addFriend(final String name, final String alias) {
        final FriendManager friendManager = Vandalism.getInstance().getFriendManager();
        for (final Friend friend : friendManager.getList()) {
            if (friend.getName().equalsIgnoreCase(name)) {
                ChatUtil.errorChatMessage("You already have a friend with the name " + name + ".");
                return;
            }
        }
        friendManager.add(new Friend(name, alias));
        ChatUtil.infoChatMessage("Added " + name + (!name.equals(alias) ? " (" + alias + ")" : "") + " as a friend.");
    }

}
