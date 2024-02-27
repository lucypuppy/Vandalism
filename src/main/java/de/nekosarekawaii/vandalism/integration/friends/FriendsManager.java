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

package de.nekosarekawaii.vandalism.integration.friends;

import com.mojang.authlib.GameProfile;
import de.florianmichael.rclasses.pattern.storage.Storage;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.event.normal.internal.TargetListener;
import de.nekosarekawaii.vandalism.event.normal.render.TextDrawListener;
import de.nekosarekawaii.vandalism.integration.friends.config.FriendConfig;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.commons.lang3.StringUtils;

public class FriendsManager extends Storage<Friend> implements TargetListener, TextDrawListener {

    public FriendsManager(final ConfigManager configManager) {
        configManager.add(new FriendConfig(this));
        Vandalism.getInstance().getEventSystem().subscribe(TargetEvent.ID, this);
        Vandalism.getInstance().getEventSystem().subscribe(TextDrawEvent.ID, this);
    }

    @Override
    public void init() {
        //  No op
    }

    @Override
    public void onTarget(final TargetEvent event) {
        if (Vandalism.getInstance().getClientSettings().getTargetSettings().ignoreFriends.getValue()) {
            return;
        }
        if (event.entity instanceof final PlayerEntity player) {
            final GameProfile gameProfile = player.getGameProfile();
            if (gameProfile == null) return;
            for (final Friend friend : this.getList()) {
                if (friend.getName().equalsIgnoreCase(gameProfile.getName())) {
                    event.isTarget = false;
                    break;
                }
            }
        }
    }

    @Override
    public void onTextDraw(final TextDrawListener.TextDrawEvent event) {
        for (final Friend friend : this.getList()) {
            event.text = StringUtils.replace(event.text, friend.getName(), friend.getAlias());
        }
    }

}
