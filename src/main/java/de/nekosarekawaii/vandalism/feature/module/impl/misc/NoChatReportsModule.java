/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.player.ChatModifyReceiveListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.encryption.ClientPlayerSession;
import net.minecraft.network.message.MessageChain;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.raphimc.vialoader.util.VersionRange;

import java.awt.*;

public class NoChatReportsModule extends Module implements PlayerUpdateListener, ChatModifyReceiveListener {

    private static final MutableText CHAT_REPORTS_REQUIRED = Text.empty()
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append(" [")
            .append(Text.literal("View Original Message").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(new Color(241, 101, 6).getRGB()))))
            .append("]");

    public NoChatReportsModule() {
        super(
                "No Chat Reports",
                "Disables cryptographic signatures for chat messages.\n\n" +
                        "If you enable this in-game you will not be able to send chat messages on servers that\n" +
                        "require cryptographic signatures until you reconnect.",
                Category.MISC,
                VersionRange.andNewer(ProtocolVersion.v1_19)
        );
    }

    @Override
    protected void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, ChatModifyReceiveEvent.ID);
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, ChatModifyReceiveEvent.ID);
        final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        if (networkHandler != null && networkHandler.session == null) {
            mc.getProfileKeys().fetchKeyPair().thenAcceptAsync(optional -> optional.ifPresent(profileKeys -> {
                networkHandler.session = ClientPlayerSession.create(profileKeys);
            }), mc);
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        if (networkHandler == null) {
            return;
        }
        networkHandler.session = null;
        networkHandler.messagePacker = MessageChain.Packer.NONE;
    }

    @Override
    public void onChatModifyReceive(final ChatModifyReceiveEvent event) {
        final Text originalText = event.mutableText;
        if (originalText.getContent() instanceof final TranslatableTextContent trContent) {
            if (!trContent.getKey().equals("chat.disabled.missingProfileKey")) {
                return;
            }
            if (originalText.getString().endsWith(CHAT_REPORTS_REQUIRED.getString())) {
                return;
            }
            final MutableText mutableText = Text.literal(ChatUtil.getChatPrefix().getString() + Formatting.RED + "The server is refusing to let you chat without enabling chat reports.");
            mutableText.append(CHAT_REPORTS_REQUIRED.copy().setStyle(
                    CHAT_REPORTS_REQUIRED.getStyle().withHoverEvent(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            originalText
                    )))
            );
            event.mutableText = mutableText;
        }
    }

    public MessageIndicator modifyIndicator(final MessageSignatureData signature, final MessageIndicator indicator) {
        if (indicator != null || signature == null) {
            return indicator;
        }
        return new MessageIndicator(
                0xE84F58,
                MessageIndicator.Icon.CHAT_MODIFIED,
                Text.literal(
                        ChatUtil.getChatPrefix().getString() + Formatting.RED + "Reportable" + Formatting.RESET +
                                " - This message has a valid signature and is thus vulnerable to fraudulent chat reports."
                ),
                "Reportable"
        );
    }

}
