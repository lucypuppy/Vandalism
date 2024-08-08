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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class ResourcePackSpooferModule extends AbstractModule implements IncomingPacketListener, OutgoingPacketListener {

    private final BooleanValue logResourcePackUrl = new BooleanValue(
            this,
            "Log Resource Pack URL",
            "Logs the Resource Pack URL to the chat.",
            true
    );

    public ResourcePackSpooferModule() {
        super(
                "Resource Pack Spoofer",
                "Allows you to spoof the Resource Pack.",
                Category.MISC
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, IncomingPacketEvent.ID, OutgoingPacketEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, IncomingPacketEvent.ID, OutgoingPacketEvent.ID);
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        if (event.packet instanceof final ResourcePackSendS2CPacket resourcePackSendS2CPacket) {
            event.cancel();
            final String resourcePackUrl = resourcePackSendS2CPacket.url();
            if (this.logResourcePackUrl.getValue()) {
                final MutableText text = Text.literal("Spoofed incoming resource pack").append(Text.literal(": ").formatted(Formatting.GRAY));
                text.append(Text.literal(resourcePackUrl).formatted(Formatting.DARK_AQUA));
                text.setStyle(text.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, resourcePackUrl)));
                final MutableText hoverText = Text.literal("Click here to open the resource pack url.");
                hoverText.formatted(Formatting.YELLOW);
                text.setStyle(text.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)));
                ChatUtil.infoChatMessage(text);
            }
            final UUID uuid = resourcePackSendS2CPacket.id();
            if (ClientCommonNetworkHandler.getParsedResourcePackUrl(resourcePackUrl) == null) {
                event.connection.send(new ResourcePackStatusC2SPacket(uuid, ResourcePackStatusC2SPacket.Status.INVALID_URL));
            }
            else {
                event.connection.send(new ResourcePackStatusC2SPacket(uuid, ResourcePackStatusC2SPacket.Status.ACCEPTED));
                event.connection.send(new ResourcePackStatusC2SPacket(uuid, ResourcePackStatusC2SPacket.Status.DOWNLOADED));
                event.connection.send(new ResourcePackStatusC2SPacket(uuid, ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
            }
        }
    }


    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        if (
                event.packet instanceof final ResourcePackStatusC2SPacket resourcePackStatusC2SPacket && (
                        resourcePackStatusC2SPacket.status() == ResourcePackStatusC2SPacket.Status.DECLINED ||
                                resourcePackStatusC2SPacket.status() == ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD
                )
        ) {
            event.cancel();
        }
    }

}
