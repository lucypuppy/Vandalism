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

package de.nekosarekawaii.vandalism.injection.mixins.clientsetting;

import com.mojang.authlib.GameProfile;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.VisualSettings;
import de.nekosarekawaii.vandalism.integration.friends.Friend;
import de.nekosarekawaii.vandalism.integration.friends.FriendsManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    @Shadow protected abstract boolean hasLabel(final Entity entity);

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;hasLabel(Lnet/minecraft/entity/Entity;)Z"))
    private boolean changeDisplayNameVisibility(final EntityRenderer instance, final Entity entity) {
        final VisualSettings visualSettings = Vandalism.getInstance().getClientSettings().getVisualSettings();
        if (entity == MinecraftClient.getInstance().player) {
            final Screen currentScreen = MinecraftClient.getInstance().currentScreen;
            if (visualSettings.showOwnDisplayName.getValue()) {
                return !(currentScreen instanceof InventoryScreen) && !(currentScreen instanceof CreativeInventoryScreen);
            }
        }
        else if (visualSettings.customDisplayNameVisibility.getValue()) {
            switch (visualSettings.displayNameVisibilityMode.getValue()) {
                case SHOW_ALL -> {
                    return true;
                }
                case HIDE_ALL -> {
                    return false;
                }
                case HIDE_FRIENDS -> {
                    if (entity instanceof final PlayerEntity playerEntity) {
                        final GameProfile gameProfile = playerEntity.getGameProfile();
                        if (gameProfile != null) {
                            final FriendsManager friendsManager = Vandalism.getInstance().getFriendsManager();
                            for (final Friend friend : friendsManager.getList()) {
                                if (friend.getName().equalsIgnoreCase(gameProfile.getName())) {
                                    return false;
                                }
                            }
                        }
                    }
                }
                default -> {}
            }
        }
        return this.hasLabel(entity);
    }

}
