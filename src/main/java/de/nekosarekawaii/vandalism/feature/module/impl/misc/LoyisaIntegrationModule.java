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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.network.WorldListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.integration.ViaFabricPlusAccess;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import de.nekosarekawaii.vandalism.util.game.server.ServerUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.raphimc.vialoader.util.VersionRange;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;

public class LoyisaIntegrationModule extends AbstractModule implements PlayerUpdateListener, WorldListener {

    private final BooleanValue prefix = new BooleanValue(
            this,
            "Prefix",
            "In-game client prefix.",
            true
    );

    private boolean send = false;

    public LoyisaIntegrationModule() {
        super(
                "Loyisa Integration",
                "Integration for the the eu.loyisa.cn server system.",
                Category.MISC,
                VersionRange.single(ProtocolVersion.v1_8)
        );

    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, WorldLoadEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, WorldLoadEvent.ID);
    }

    @Override
    public void onPreWorldLoad() {
        this.send = false;
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.send) return;
        if (this.mc.player == null) return;
        if (!ServerUtil.lastServerExists() && ServerUtil.getLastServerInfo().address.equals("eu.loyisa.cn")) {
            return;
        }
        if (!ProtocolTranslator.getTargetVersion().equalTo(ProtocolVersion.v1_8)) {
            ChatUtil.errorChatMessage("You are not on 1.8, Loyisa integration is disabled.");
            return;
        }
        ChatUtil.infoChatMessage("Loyisa integration enabled.");
        final String clientName = "Vandalism";
        final String username = mc.session.getUsername();
        final String actionToSend = this.prefix.getValue() ? "ADD" : "REMOVE";
        final long timestamp = System.currentTimeMillis();
        final String key = "vandalism";
        final String md5 = DigestUtils.md5Hex(clientName + username + actionToSend + timestamp + key);
        final ByteBuf buf = Unpooled.buffer();
        final String payload = ">" + clientName + "|" + username + "|" + actionToSend + "|" + timestamp + "|" + md5 + "<";
        buf.writeBytes(payload.getBytes(StandardCharsets.UTF_8));
        ViaFabricPlusAccess.send1_8CustomPayload("Loyisa|Prefix", buf);
        this.send = true;
    }

}
