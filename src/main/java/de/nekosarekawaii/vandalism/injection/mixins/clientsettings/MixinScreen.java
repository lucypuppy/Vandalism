/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.MenuSettings;
import de.nekosarekawaii.vandalism.integration.render.shader.Shaders;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow
    public int width;

    @Shadow
    public int height;

    @Shadow
    @Nullable
    protected MinecraftClient client;

    @Shadow
    public abstract void renderBackgroundTexture(DrawContext context);

    @Inject(method = "renderBackgroundTexture", at = @At("HEAD"), cancellable = true)
    private void drawCustomBackgroundInGui(final DrawContext context, final CallbackInfo ci) {
        final MenuSettings menuSettings = Vandalism.getInstance().getClientSettings().getMenuSettings();

        if (this.client.world != null) return;

        if (menuSettings.backgroundMode.getValue() == MenuSettings.BackgroundMode.COLOR) {
            ci.cancel();
            context.fill(
                    0,
                    0,
                    this.width,
                    this.height,
                    menuSettings.customBackgroundColor.getColor().getRGB()
            );
        } else if (menuSettings.backgroundMode.getValue() == MenuSettings.BackgroundMode.SHADER) {
            ci.cancel();
            Shaders.BACKGROUND.getShader().drawOnScreen(context.getMatrices(), shader -> {
                shader.setUniform("resolution", new Vec2f(client.getWindow().getWidth(), client.getWindow().getHeight()));
                shader.setUniform("time", (System.currentTimeMillis() - shader.getStartTime()) / 1000.0f);
            });
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
                    menuSettings.inGameCustomBackgroundColorTop.getColor().getRGB(),
                    menuSettings.inGameCustomBackgroundColorBottom.getColor().getRGB()
            );
        }
    }

}
