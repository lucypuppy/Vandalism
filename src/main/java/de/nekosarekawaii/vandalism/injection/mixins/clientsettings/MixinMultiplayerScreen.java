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

import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.integration.serverlist.ServerList;
import de.nekosarekawaii.vandalism.integration.serverlist.gui.ConfigScreen;
import de.nekosarekawaii.vandalism.util.common.UUIDUtil;
import net.lenni0451.mcping.MCPing;
import net.lenni0451.mcping.responses.MCPingResponse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.session.Session;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.handshake.ConnectionIntent;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.profiler.PerformanceLog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen extends Screen {

    @Shadow
    protected abstract void refresh();

    @Shadow
    private net.minecraft.client.option.ServerList serverList;

    @Shadow
    protected MultiplayerServerListWidget serverListWidget;

    protected MixinMultiplayerScreen(final Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void addEnhancedServerListButton(final CallbackInfo ci) {
        if (Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings().enhancedServerList.getValue()) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Server Lists"), button -> {
                if (this.client != null) {
                    this.client.setScreen(new ConfigScreen((MultiplayerScreen) (Object) this));
                }
            }).dimensions(4, 4, 100, 20).build());
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void enhancedServerListSyncServerList(final CallbackInfo ci) {
        if (Vandalism.getInstance().getServerListManager().hasBeenChanged()) {
            this.refresh();
        }
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawCenteredTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"))
    private void enhancedServerListModifyTitle(final Args args) {
        if (!Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings().enhancedServerList.getValue()) {
            return;
        }
        final ServerList selectedServerList = Vandalism.getInstance().getServerListManager().getSelectedServerList();
        final MutableText title = Text.literal(selectedServerList.isDefault() ? ServerList.DEFAULT_SERVER_LIST_NAME : selectedServerList.getName());
        title.append(" (" + selectedServerList.getSize() + ") | ");
        title.append((Text) args.get(1));
        args.set(1, title);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings().enhancedServerList.getValue()) {
            if (Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings().kickAllPlayersKey.getValue() == keyCode) {
                if (Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings().kickAllPlayers.getValue()) {
                    final MultiplayerServerListWidget.Entry selectedEntry = this.serverListWidget.getSelectedOrNull();
                    if (selectedEntry instanceof final MultiplayerServerListWidget.ServerEntry selectedServerEntry) {
                        final ServerInfo serverInfo = selectedServerEntry.getServer();
                        if (serverInfo != null) {
                            String host = serverInfo.address;
                            int port = 25565;
                            if (host.contains(":")) {
                                final String[] data = host.split(":");
                                host = data[0];
                                port = Integer.parseInt(data[1]);
                            }
                            MCPing.pingModern(ProtocolTranslator.getTargetVersion().getVersion()).address(host, port)
                                    .timeout(
                                            Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings().kickAllPlayersPingConnectionTimeout.getValue(),
                                            Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings().kickAllPlayersPingReadTimeout.getValue()
                                    )
                                    .exceptionHandler(t -> Vandalism.getInstance().getLogger().error("Failed to kick any player cause of an error while pinging the server.", t))
                                    .finishHandler(response -> {
                                        final MCPingResponse.Players playersData = response.players;
                                        if (playersData.online > 0) {
                                            final MCPingResponse.Players.Player[] players = playersData.sample;
                                            if (players != null && players.length > 0) {
                                                final Session session = this.client.session;
                                                for (final MCPingResponse.Players.Player player : players) {
                                                    if (player != null) {
                                                        final String name = player.name;
                                                        final String id = player.id;
                                                        if (!name.isBlank()) {
                                                            try {
                                                                Vandalism.getInstance().getLogger().info("Kicking " + name + "...");
                                                                String uuid;
                                                                try {
                                                                    uuid = UUIDUtil.getUUIDFromName(name);
                                                                } catch (Exception e) {
                                                                    uuid = id;
                                                                    Vandalism.getInstance().getLogger().error("Failed to get UUID of the player: \"" + name + "\" (using fallback UUID).");
                                                                }
                                                                this.client.session = new Session(
                                                                        name,
                                                                        UUID.fromString(uuid),
                                                                        "",
                                                                        Optional.of(""),
                                                                        Optional.of(""),
                                                                        Session.AccountType.LEGACY
                                                                );
                                                                final ClientConnection clientConnection = ClientConnection.connect(new InetSocketAddress(response.server.ip, response.server.port), true, (PerformanceLog) null);
                                                                clientConnection.send(new HandshakeC2SPacket(response.server.protocol, response.server.ip, response.server.port, ConnectionIntent.LOGIN));
                                                                clientConnection.send(new LoginHelloC2SPacket(name, UUID.fromString(uuid)));
                                                                Vandalism.getInstance().getLogger().info("Player " + name + " should be kicked.");
                                                                Thread.sleep(Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings().kickAllPlayersKickDelay.getValue());
                                                            } catch (Exception e) {
                                                                Vandalism.getInstance().getLogger().error("Failed to kick the player: \"" + name + "\"", e);
                                                            }
                                                        }
                                                    }
                                                }
                                                this.client.session = session;
                                            } else {
                                                Vandalism.getInstance().getLogger().error("There are no players to kick online.");
                                            }
                                        } else {
                                            Vandalism.getInstance().getLogger().error("There are no players to kick online.");
                                        }
                                    }).getAsync();
                        }
                    }
                }
            }
            if (Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings().refreshServerListKey.getValue() == keyCode) {
                this.refresh();
            }
            if (Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings().pasteServerKey.getValue() == keyCode) {
                String clipboard = this.client.keyboard.getClipboard();
                if (clipboard != null && !clipboard.isBlank()) {
                    clipboard = clipboard.replaceAll("[^a-zA-Z0-9.:-_]", "");
                    final ServerInfo serverInfo = new ServerInfo(
                            "Copied from Clipboard",
                            clipboard,
                            ServerInfo.ServerType.OTHER
                    );
                    this.serverList.add(serverInfo, false);
                    this.serverList.saveFile();
                    this.refresh();
                }
            }
            final MultiplayerServerListWidget.Entry selectedEntry = this.serverListWidget.getSelectedOrNull();
            if (selectedEntry instanceof final MultiplayerServerListWidget.ServerEntry selectedServerEntry) {
                final ServerInfo serverInfo = selectedServerEntry.getServer();
                if (Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings().copyServerKey.getValue() == keyCode) {
                    this.client.keyboard.setClipboard(serverInfo.address);
                }
                if (Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings().deleteServerKey.getValue() == keyCode) {
                    this.serverList.remove(serverInfo);
                    this.serverList.saveFile();
                    this.refresh();
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

}
