/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

package de.nekosarekawaii.vandalism.addonthirdparty.ethanol.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.addonthirdparty.ethanol.module.impl.CommandEthanolCustomPayload;
import de.nekosarekawaii.vandalism.addonthirdparty.ethanol.module.impl.InitEthanolCustomPayload;
import de.nekosarekawaii.vandalism.addonthirdparty.ethanol.module.impl.MessageEthanolCustomPayload;
import de.nekosarekawaii.vandalism.addonthirdparty.ethanol.module.impl.VanishEthanolCustomPayload;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.StringValue;
import de.nekosarekawaii.vandalism.event.network.WorldListener;
import de.nekosarekawaii.vandalism.event.player.ChatSendListener;
import de.nekosarekawaii.vandalism.event.render.Render2DListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.text.Text;

public class EthanolModule extends AbstractModule implements ChatSendListener, Render2DListener, WorldListener {

    public EthanolModule() {
        super("Ethanol", "Implementation for the communication of the Ethanol plugin.", Category.MISC);
        PayloadTypeRegistry.playC2S().register(InitEthanolCustomPayload.ID, InitEthanolCustomPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(CommandEthanolCustomPayload.ID, CommandEthanolCustomPayload.CODEC);

        PayloadTypeRegistry.playS2C().register(InitEthanolCustomPayload.ID, InitEthanolCustomPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(VanishEthanolCustomPayload.ID, VanishEthanolCustomPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(MessageEthanolCustomPayload.ID, MessageEthanolCustomPayload.CODEC);
    }

    private final StringValue commandPrefix = new StringValue(
            this,
            "Command prefix",
            "Ethanol command prefix.",
            "-"
    );

    public boolean vanished = false, detected = false;

    @Override
    public void onChatSend(ChatSendEvent event) {
        if (!this.detected) return;

        final String message = event.message;
        final String prefix = this.commandPrefix.getValue();
        if (message.startsWith(prefix)) {
            event.message = "";
            final String command = message.substring(prefix.length());
            if (!command.isEmpty()) {
                this.mc.inGameHud.getChatHud().addToMessageHistory(message);
                this.mc.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new CommandEthanolCustomPayload(command)));
            }
        }
    }

    @Override
    public void onPreWorldLoad() {
        this.detected = false;
        this.vanished = false;
    }

    @Override
    public void onRender2DInGame(DrawContext context, float delta) {
        if (this.detected) {
            {
                final Text text = Text.literal("Ethanol detected");

                context.drawText(
                        this.mc.textRenderer,
                        text,
                        context.getScaledWindowWidth() - this.mc.textRenderer.getWidth(text),
                        context.getScaledWindowHeight() - this.mc.textRenderer.fontHeight,
                        0xFFFFFF,
                        true
                );
            }

            if (this.vanished) {
                final Text text = Text.literal("Vanished").withColor(0xFF0000);

                context.drawText(
                        this.mc.textRenderer,
                        text,
                        context.getScaledWindowWidth() - this.mc.textRenderer.getWidth(text),
                        context.getScaledWindowHeight() - (this.mc.textRenderer.fontHeight * 2),
                        0xFFFFFF,
                        true
                );
            }
        }
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, WorldLoadEvent.ID, ChatSendEvent.ID, Render2DEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, WorldLoadEvent.ID, ChatSendEvent.ID, Render2DEvent.ID);
    }

}
