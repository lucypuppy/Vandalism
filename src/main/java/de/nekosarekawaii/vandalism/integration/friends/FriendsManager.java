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

package de.nekosarekawaii.vandalism.integration.friends;

import com.mojang.authlib.GameProfile;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.clientwindow.ClientWindowManager;
import de.nekosarekawaii.vandalism.event.internal.TargetListener;
import de.nekosarekawaii.vandalism.event.render.TextDrawListener;
import de.nekosarekawaii.vandalism.integration.friends.config.FriendsConfig;
import de.nekosarekawaii.vandalism.integration.friends.gui.FriendsClientWindow;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import de.nekosarekawaii.vandalism.util.storage.Storage;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.commons.lang3.StringUtils;

public class FriendsManager extends Storage<Friend> implements TargetListener, TextDrawListener {

    public FriendsManager(final ConfigManager configManager, final ClientWindowManager clientWindowManager) {
        configManager.add(new FriendsConfig(this));
        clientWindowManager.add(new FriendsClientWindow(this));
        Vandalism.getInstance().getEventSystem().subscribe(TargetEvent.ID, this);
        Vandalism.getInstance().getEventSystem().subscribe(TextDrawEvent.ID, this);
    }

    @Override
    public void init() {
        
    }

    @Override
    public void onTarget(final TargetEvent event) {
        if (event.entity instanceof final PlayerEntity player) {
            final GameProfile gameProfile = player.getGameProfile();
            if (gameProfile == null) {
                return;
            }
            if (this.isFriend(gameProfile.getName(), true)) {
                event.isTarget = false;
            }
        }
    }

    @Override
    public void onTextDraw(final TextDrawListener.TextDrawEvent event) {
        for (final Friend friend : this.getList()) {
            event.text = StringUtils.replace(event.text, friend.getName(), friend.getAlias());
        }
    }

    public void addFriend(final String name, final String alias) {
        if (this.getList().stream().anyMatch(friend -> friend.getName().equalsIgnoreCase(name))) {
            ChatUtil.errorChatMessage("You already have a friend with the name " + name + ".");
            return;
        }

        this.add(new Friend(name, alias));
        ChatUtil.infoChatMessage("Added " + name + (!name.equals(alias) ? " (" + alias + ")" : "") + " as a friend.");
    }

    public void removeFriend(final String name) {
        this.getList().stream().filter(friend -> friend.getName().equalsIgnoreCase(name)).findFirst().ifPresentOrElse(friend -> {
            this.remove(friend);
            ChatUtil.infoChatMessage("Removed " + name + " as a friend.");
        }, () -> ChatUtil.errorChatMessage("You don't have a friend with the name " + name + "."));
    }

    public void removeFriend(final Friend friend) {
        if (this.getList().contains(friend)) {
            this.remove(friend);
            ChatUtil.infoChatMessage("Removed " + friend.getName() + " as a friend.");
            return;
        }
        ChatUtil.errorChatMessage("You don't have a friend with the name " + friend.getName() + ".");
    }

    public boolean isFriend(final String name) {
        return this.isFriend(name, false);
    }

    public boolean isFriend(final String name, final boolean checkNoFriendsModule) {
        if (checkNoFriendsModule) {
            if (Vandalism.getInstance().getModuleManager().getNoFriendsModule().isActive()) {
                return false;
            }
        }
        return this.getList().stream().anyMatch(friend -> friend.getName().equalsIgnoreCase(name));
    }

}
