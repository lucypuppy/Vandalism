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

package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import de.florianmichael.rclasses.common.color.ColorUtils;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.MenuSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow public int width;

    @Shadow public int height;

    @Shadow @Nullable protected MinecraftClient client;

    @Inject(method = "renderBackgroundTexture", at = @At("HEAD"), cancellable = true)
    private void drawCustomBackgroundInGui(final DrawContext context, final CallbackInfo ci) {
        final MenuSettings menuSettings = Vandalism.getInstance().getClientSettings().getMenuSettings();
        if (menuSettings.customBackground.getValue()) {
            ci.cancel();
            context.fill(
                    0,
                    0,
                    this.width,
                    this.height,
                    ColorUtils.withAlpha(menuSettings.customBackgroundColor.getColor(), this.client.player == null ? 255 : 100).getRGB()
            );
        }
    }

    @Inject(method = "renderInGameBackground", at = @At("HEAD"), cancellable = true)
    private void drawCustomBackgroundInGame(final DrawContext context, final CallbackInfo ci) {
        final MenuSettings menuSettings = Vandalism.getInstance().getClientSettings().getMenuSettings();
        if (menuSettings.inGameCustomBackground.getValue()) {
            ci.cancel();
            context.fillGradient(
                    0,
                    0,
                    this.width,
                    this.height,
                    ColorUtils.withAlpha(Color.BLACK, 100).getRGB(),
                    ColorUtils.withAlpha(menuSettings.inGameCustomBackgroundColor.getColor(), this.client.player == null ? 255 : 100).getRGB()
            );
        }
    }

}
