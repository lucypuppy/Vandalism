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

import de.florianmichael.rclasses.math.timer.MSTimer;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.EnhancedServerListSettings;
import de.nekosarekawaii.vandalism.integration.serverlist.ServerDataUtil;
import de.nekosarekawaii.vandalism.integration.serverlist.ServerPingerWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DirectConnectScreen.class)
public abstract class MixinDirectConnectScreen extends Screen {

    @Shadow
    private TextFieldWidget addressField;

    @Unique
    private final MSTimer vandalism$pingTimer = new MSTimer();

    @Unique
    private String vandalism$lastAddress = "";

    protected MixinDirectConnectScreen(final Text ignored) {
        super(ignored);
    }

    @Inject(method = "render", at = @At(value = "RETURN"))
    private void drawServerPingerWidget(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        final String address = this.addressField.getText();
        if (address.isEmpty()) return;
        ServerPingerWidget.draw(new ServerInfo(address, address, ServerInfo.ServerType.OTHER), context, mouseX, mouseY, delta, 30);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawCenteredTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"))
    private void removeTitle(final DrawContext instance, final TextRenderer textRenderer, final Text text, final int centerX, final int y, final int color) {
        final EnhancedServerListSettings enhancedServerListSettings = Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings();
        if (enhancedServerListSettings.enhancedServerList.getValue() && enhancedServerListSettings.serverPingerWidget.getValue()) {
            return;
        }
        instance.drawCenteredTextWithShadow(textRenderer, text, centerX, y, color);
    }

    @Inject(method = "onAddressFieldChanged", at = @At(value = "RETURN"))
    private void resetPingTimer(final CallbackInfo ci) {
        final EnhancedServerListSettings enhancedServerListSettings = Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings();
        if (enhancedServerListSettings.enhancedServerList.getValue() && enhancedServerListSettings.serverPingerWidget.getValue()) {
            this.vandalism$pingTimer.reset();
        }
    }

    @Override
    public void tick() {
        super.tick();
        final EnhancedServerListSettings enhancedServerListSettings = Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings();
        if (enhancedServerListSettings.enhancedServerList.getValue()) {
            String address = this.addressField.getText();
            if (address != null && !address.isBlank()) {
                if (enhancedServerListSettings.directConnectAddressFix.getValue()) {
                    final String oldAddress = address;
                    address = ServerDataUtil.fixAddress(address);
                    if (!oldAddress.equals(address)) {
                        this.addressField.setText(address);
                    }
                }
                if (enhancedServerListSettings.serverPingerWidget.getValue()) {
                    if (this.vandalism$pingTimer.hasReached(1000, true)) {
                        if (address.equals(this.vandalism$lastAddress)) return;
                        this.vandalism$lastAddress = address;
                        ServerPingerWidget.ping(new ServerInfo(address, address, ServerInfo.ServerType.OTHER));
                        this.vandalism$pingTimer.reset();
                    }
                }
            }
        }
    }

}
