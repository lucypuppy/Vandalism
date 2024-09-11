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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.util.ServerUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DownloadingTerrainScreen.class)
public abstract class MixinDownloadingTerrainScreen extends Screen {

    @Unique
    private static final String vandalism$CANCEL_MESSAGE = "Press [ESC] to cancel.";

    @Unique
    private static final String vandalism$SHUTDOWN_MESSAGE = FabricBootstrap.MOD_NAME + " is shutting down...";

    protected MixinDownloadingTerrainScreen(final Text ignored) {
        super(ignored);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderEscapingText(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        if (!FabricBootstrap.SHUTTING_DOWN) {
            if (Vandalism.getInstance().getClientSettings().getMenuSettings().downloadingTerrainScreenEscaping.getValue()) {
                context.drawCenteredTextWithShadow(this.textRenderer, vandalism$CANCEL_MESSAGE, this.width / 2, this.height / 2 - 50 + this.textRenderer.fontHeight, 0xFFFFFF);
            }
        }
        else {
            context.drawCenteredTextWithShadow(this.textRenderer, vandalism$SHUTDOWN_MESSAGE, this.width / 2, this.height / 2 - 50 + this.textRenderer.fontHeight, 0xFFFFFF);
        }
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (!FabricBootstrap.SHUTTING_DOWN) {
            if (Vandalism.getInstance().getClientSettings().getMenuSettings().downloadingTerrainScreenEscaping.getValue()) {
                if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                    ServerUtil.disconnect("Manually disconnected from server.");
                    return true;
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

}
