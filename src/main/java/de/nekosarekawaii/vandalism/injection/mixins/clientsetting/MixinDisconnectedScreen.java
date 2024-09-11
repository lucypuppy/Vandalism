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
import de.nekosarekawaii.vandalism.base.clientsettings.impl.MenuSettings;
import de.nekosarekawaii.vandalism.integration.serverlist.ServerPingerWidget;
import de.nekosarekawaii.vandalism.util.MSTimer;
import de.nekosarekawaii.vandalism.util.ServerUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.LoadingDisplay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DisconnectedScreen.class)
public abstract class MixinDisconnectedScreen extends Screen {

    @Shadow
    @Final
    private Screen parent;

    @Unique
    private final MSTimer vandalism$reconnectTimer = new MSTimer();

    @Unique
    private long vandalism$oldAutoReconnectDelay = 0L;

    protected MixinDisconnectedScreen(final Text ignored) {
        super(ignored);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        final MenuSettings menuSettings = Vandalism.getInstance().getClientSettings().getMenuSettings();
        if (menuSettings.disconnectedScreenEscaping.getValue()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                this.client.setScreen(this.parent);
            }
        }
        if (menuSettings.disconnectedScreenCopyData.getValue()) {
            if (isCopy(keyCode)) {
                final StringBuilder textBuilder = new StringBuilder(ServerUtil.lastServerExists() ? "Disconnect Data from " + ServerUtil.getLastServerInfo().address : "");
                final String emptyLine = "\n\n";
                textBuilder.append(emptyLine);
                children().forEach(w -> {
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
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick() {
        super.tick();
        final MenuSettings menuSettings = Vandalism.getInstance().getClientSettings().getMenuSettings();
        if (menuSettings.moreDisconnectedScreenButtons.getValue() && menuSettings.autoReconnect.getValue()) {
            final long autoReconnectDelay = Math.max(menuSettings.autoReconnectDelay.getValue() + 1, 1) * 1000L;
            if (this.vandalism$oldAutoReconnectDelay != autoReconnectDelay) {
                this.vandalism$oldAutoReconnectDelay = autoReconnectDelay;
                this.vandalism$reconnectTimer.reset();
            }
            if (this.vandalism$reconnectTimer.hasReached(autoReconnectDelay, true)) {
                ServerUtil.connectToLastServer();
            }
        }
        else {
            this.vandalism$reconnectTimer.reset();
        }
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.render(context, mouseX, mouseY, delta);
        final MenuSettings menuSettings = Vandalism.getInstance().getClientSettings().getMenuSettings();
        if (menuSettings.moreDisconnectedScreenButtons.getValue() && menuSettings.autoReconnect.getValue()) {
            final long countdownTime = Math.max(menuSettings.autoReconnectDelay.getValue() + 1, 1) * 1000L;
            final long timerDelta = this.vandalism$reconnectTimer.getDelta();
            final long remainingTime = (timerDelta < countdownTime ? countdownTime - timerDelta : 0) / 1000;
            final int x = this.width / 2;
            final int y = 15;
            final int y2 = y + this.textRenderer.fontHeight + 2;
            context.drawCenteredTextWithShadow(this.textRenderer, "Reconnecting in " + remainingTime, x, y, -1);
            context.drawCenteredTextWithShadow(this.textRenderer, LoadingDisplay.get(Util.getMeasuringTimeMs()), x, y2, -1);
        }
        ServerPingerWidget.draw(ServerUtil.getLastServerInfo(), context, mouseX, mouseY, delta, this.height - 55);
    }

    @Override
    public void removed() {
        super.removed();
        this.vandalism$reconnectTimer.reset();
    }

    @Override
    public void close() {
        super.close();
        this.vandalism$reconnectTimer.reset();
    }

    @Redirect(method = "method_60868", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/DirectionalLayoutWidget;add(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;"))
    private <T extends Widget> T removeReportToServerButton(final DirectionalLayoutWidget instance, final T widget) {
        return widget;
    }

    @Redirect(method = "method_60870", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/DirectionalLayoutWidget;add(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;"))
    private <T extends Widget> T removeOpenReportDirButton(final DirectionalLayoutWidget instance, final T widget) {
        return widget;
    }

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/DirectionalLayoutWidget;add(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;", ordinal = 2))
    private <T extends Widget> T addMoreButtons(final DirectionalLayoutWidget instance, final T widget) {
        instance.add(widget);
        this.vandalism$reconnectTimer.resume();
        ServerPingerWidget.ping(ServerUtil.getLastServerInfo());
        final MenuSettings menuSettings = Vandalism.getInstance().getClientSettings().getMenuSettings();
        if (menuSettings.moreDisconnectedScreenButtons.getValue()) {
            final Positioner positioner = instance.getMainPositioner().copy();
            instance.add(ButtonWidget.builder(Text.literal("Reconnect"), button -> ServerUtil.connectToLastServer()).width(200).build(), positioner);
            instance.add(ButtonWidget.builder(Text.literal("Auto Reconnect: " + (menuSettings.autoReconnect.getValue() ? "On" : "Off")), button -> {
                menuSettings.autoReconnect.setValue(!menuSettings.autoReconnect.getValue());
                button.setMessage(Text.literal("Auto Reconnect: " + (menuSettings.autoReconnect.getValue() ? "On" : "Off")));
            }).width(200).build(), positioner);
        }
        return widget;
    }

}
