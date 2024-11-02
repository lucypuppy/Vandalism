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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import com.mojang.authlib.GameProfile;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.game.MouseInputListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

public class MiddleClickFriendsModule extends Module implements MouseInputListener {

    public MiddleClickFriendsModule() {
        super(
                "Middle Click Friends",
                "Lets you add or remove friends.",
                Category.MISC
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(MouseEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(MouseEvent.ID, this);
    }

    @Override
    public void onMouse(final MouseEvent event) {
        if (mc.currentScreen != null) {
            return;
        }
        if (event.type == MouseInputListener.Type.BUTTON) {
            if (event.button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE && event.action == GLFW.GLFW_PRESS) {
                final HitResult hitResult = mc.crosshairTarget;
                if (hitResult instanceof final EntityHitResult entityHitResult) {
                    final Entity entity = entityHitResult.getEntity();
                    if (entity != null) {
                        final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
                        if (networkHandler != null) {
                            final PlayerListEntry playerListEntry = networkHandler.getPlayerListEntry(entity.getUuid());
                            if (playerListEntry != null) {
                                final GameProfile gameProfile = playerListEntry.getProfile();
                                if (gameProfile != null) {
                                    final String name = gameProfile.getName();
                                    if (name != null) {
                                        if (Vandalism.getInstance().getFriendsManager().isFriend(name)) {
                                            Vandalism.getInstance().getFriendsManager().removeFriend(name);
                                        } else {
                                            Vandalism.getInstance().getFriendsManager().addFriend(name, name);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
