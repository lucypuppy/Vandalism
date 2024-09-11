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

import com.mojang.blaze3d.systems.RenderSystem;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.MenuSettings;
import de.nekosarekawaii.vandalism.util.render.util.ColorUtils;
import de.nekosarekawaii.vandalism.util.render.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;

@Mixin(PressableWidget.class)
public abstract class MixinPressableWidget extends ClickableWidget {

    public MixinPressableWidget(final int x, final int y, final int width, final int height, final Text message) {
        super(x, y, width, height, message);
    }

    @Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V"), remap = false)
    private void cancelBlend() {
        if (!Vandalism.getInstance().getClientSettings().getMenuSettings().customWidgets.getValue()) {
            RenderSystem.enableBlend();
        }
    }

    @Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableDepthTest()V"), remap = false)
    private void cancelDepthTest() {
        if (!Vandalism.getInstance().getClientSettings().getMenuSettings().customWidgets.getValue()) {
            RenderSystem.enableDepthTest();
        }
    }

    @Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
    private void drawClientButton(final DrawContext drawContext, final Identifier identifier, final int x, final int y, final int u, final int v) {
        final MenuSettings menuSettings = Vandalism.getInstance().getClientSettings().getMenuSettings();
        if (!menuSettings.customWidgets.getValue()) {
            drawContext.drawGuiTexture(identifier, x, y, u, v);
            return;
        }
        final boolean selected = this.isSelected();
        final int selectionOffset = selected ? menuSettings.customWidgetsSelectionOffset.getValue() : 0;
        Color mainColor = menuSettings.customWidgetsMainColor.getColor();
        Color secondaryColor = menuSettings.customWidgetsSecondaryColor.getColor();
        if (!this.active) {
            mainColor = ColorUtils.withAlpha(mainColor, Math.max(mainColor.getAlpha() / 2, 0));
            secondaryColor = ColorUtils.withAlpha(secondaryColor, Math.max(secondaryColor.getAlpha() / 2, 0));
        }
        RenderUtil.fillOutlined(
                drawContext,
                x,
                y + selectionOffset,
                x + this.width,
                y + this.height - selectionOffset,
                menuSettings.customWidgetsOutlineWidth.getValue(),
                mainColor.getRGB(),
                secondaryColor.getRGB()
        );
    }

}
