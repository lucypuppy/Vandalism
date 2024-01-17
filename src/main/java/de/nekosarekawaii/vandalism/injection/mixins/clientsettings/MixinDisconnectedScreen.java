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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.util.game.ServerUtil;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DisconnectedScreen.class)
public abstract class MixinDisconnectedScreen extends Screen {

    @Shadow
    @Final
    private Screen parent;

    protected MixinDisconnectedScreen(final Text ignored) {
        super(ignored);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (Vandalism.getInstance().getClientSettings().getMenuSettings().disconnectedScreenEscaping.getValue()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                this.client.setScreen(this.parent);
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/DirectionalLayoutWidget;add(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;", ordinal = 2))
    private <T extends Widget> T addMoreButtons(final DirectionalLayoutWidget instance, final T widget) {
        instance.add(widget);
        if (Vandalism.getInstance().getClientSettings().getMenuSettings().moreDisconnectedScreenButtons.getValue()) {
            final Positioner positioner = instance.getMainPositioner().copy().marginTop(-8);
            instance.add(ButtonWidget.builder(Text.literal("Reconnect"), button -> ServerUtil.connectToLastServer()).build(), positioner);
            instance.add(ButtonWidget.builder(Text.literal("Copy Message"), button -> {
                final StringBuilder textBuilder = new StringBuilder(ServerUtil.lastServerExists() ? "Disconnect Message from " + ServerUtil.getLastServerInfo().address : "");
                final String emptyLine = "\n\n";
                textBuilder.append(emptyLine);
                instance.forEachElement(w -> {
                    if (w instanceof final TextWidget textWidget) {
                        textBuilder.append("[Title]").append(emptyLine).append(textWidget.getMessage().getString());
                    } else if (w instanceof final MultilineTextWidget multilineTextWidget) {
                        textBuilder.append(emptyLine).append("[Reason]").append(emptyLine).append(multilineTextWidget.getMessage().getString());
                    }
                });
                final String text = textBuilder.toString();
                if (!text.isBlank()) {
                    this.client.keyboard.setClipboard(text);
                }
            }).build(), positioner);
        }
        return widget;
    }

}
