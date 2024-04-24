/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.integration.hud.impl;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.rclasses.math.geometry.Alignment;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.event.cancellable.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.TickBaseModule;
import de.nekosarekawaii.vandalism.injection.access.IRenderTickCounter;
import de.nekosarekawaii.vandalism.integration.hud.HUDElement;
import de.nekosarekawaii.vandalism.util.click.CPSTracker;
import de.nekosarekawaii.vandalism.util.game.ServerConnectionUtil;
import de.nekosarekawaii.vandalism.util.game.WorldUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.util.Formatting;

import java.util.LinkedHashMap;
import java.util.Map;

public class InfoHUDElement extends HUDElement implements IncomingPacketListener, PlayerUpdateListener {

    private final BooleanValue shadow = new BooleanValue(
            this,
            "Shadow",
            "Whether or not the text should have a shadow.",
            true
    );

    private final ColorValue color = new ColorValue(
            this,
            "Color",
            "The color of the text."
    );

    private final BooleanValue fps = new BooleanValue(
            this,
            "FPS",
            "Shows the current fps.",
            true
    );

    private final BooleanValue cps = new BooleanValue(
            this,
            "CPS",
            "Shows the current cps.",
            true
    );

    private final BooleanValue username = new BooleanValue(
            this,
            "Username",
            "Shows the current username.",
            true
    );


    private final ValueGroup positionElements = new ValueGroup(
            this,
            "Position Elements",
            "Elements that are shown in the position category."
    );

    private final BooleanValue position = new BooleanValue(
            this.positionElements,
            "Position",
            "Shows the current position.",
            true
    );

    private final BooleanValue dimensionalPosition = new BooleanValue(
            this.positionElements,
            "Dimensional Position",
            "Shows the current position of the dimension you are currently playing in.",
            true
    );

    private final IntegerValue positionDecimalPlaces = new IntegerValue(
            this.positionElements,
            "Position Decimal Places",
            "Allows you to change the viewable amount of decimal places from the x/y/z position.",
            2,
            1,
            15
    ).visibleCondition(() -> this.position.getValue() || this.dimensionalPosition.getValue());

    private final BooleanValue difficulty = new BooleanValue(
            this,
            "Difficulty",
            "Shows the current world difficulty.",
            true
    );

    private final BooleanValue permissionsLevel = new BooleanValue(
            this,
            "Permissions Level",
            "Shows the current permissions level.",
            true
    );

    private final ValueGroup serverElements = new ValueGroup(
            this,
            "Server Elements",
            "Elements that are shown in the server category."
    );

    private final BooleanValue serverBrand = new BooleanValue(
            this.serverElements,
            "Server Brand",
            "Shows the current server brand.",
            true
    );

    private final BooleanValue serverAddress = new BooleanValue(
            this.serverElements,
            "Server Address",
            "Shows the current server address.",
            true
    );

    private final BooleanValue ping = new BooleanValue(
            this,
            "Ping",
            "Shows your current ping.",
            true
    );

    private final BooleanValue fasterPings = new BooleanValue(
            this,
            "Faster Pings",
            "This tries to send pings faster. (1.20.2+ client version)",
            false
    ).visibleCondition(this.ping::getValue);

    private final IntegerValue pingInterval = new IntegerValue(
            this,
            "Ping Interval",
            "The interval in milliseconds for the ping.",
            1000,
            0,
            10000
    ).visibleCondition(() -> this.ping.getValue() && this.fasterPings.getValue());

    private final BooleanValue packetsSent = new BooleanValue(
            this,
            "Packets Sent",
            "Shows the current packets sent.",
            true
    );

    private final BooleanValue packetsReceived = new BooleanValue(
            this,
            "Packets Received",
            "Shows the current packets received.",
            true
    );

    private final BooleanValue lagMeter = new BooleanValue(
            this,
            "Lag Meter",
            "Shows the current server lags in seconds.",
            true
    );

    private final IntegerValue lagMeterThreshold = new IntegerValue(
            this,
            "Lag Meter Threshold",
            "The threshold in milliseconds for the lag meter to show.",
            1000,
            500,
            10000
    ).visibleCondition(this.lagMeter::getValue);

    private final BooleanValue tickBaseCharge = new BooleanValue(
            this,
            "Tick Base Charge",
            "Shows the current tick base charge.",
            true
    );

    private final ValueGroup debugElements = new ValueGroup(
            this,
            "Debug Elements",
            "Elements that are shown in the debug category."
    );

    private final BooleanValue clientTPS = new BooleanValue(
            this.debugElements,
            "Client TPS",
            "Shows the current client TPS.",
            false
    );

    public final CPSTracker leftClick = new CPSTracker();
    public final CPSTracker rightClick = new CPSTracker();

    private long lastUpdate = System.currentTimeMillis();
    private long lastPing = -1;
    private long clientPing = -1;

    public InfoHUDElement() {
        super("Info");
        Vandalism.getInstance().getEventSystem().subscribe(this, IncomingPacketEvent.ID, PlayerUpdateEvent.ID);
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        final long now = System.currentTimeMillis();

        if (event.packet instanceof KeepAliveS2CPacket || event.packet instanceof WorldTimeUpdateS2CPacket) {
            this.lastUpdate = now;
        }

        if (event.packet instanceof final PingResultS2CPacket packet &&
                this.ping.getValue() &&
                this.fasterPings.getValue() &&
                !Vandalism.getInstance().getTargetVersion().olderThan(ProtocolVersion.v1_20_2)) {
            this.clientPing = now - packet.getStartTime();
        }
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        final long now = System.currentTimeMillis();

        if (now - this.lastPing > this.pingInterval.getValue() &&
                this.fasterPings.getValue() &&
                this.ping.getValue() &&
                !Vandalism.getInstance().getTargetVersion().olderThan(ProtocolVersion.v1_20_2)) {
            this.mc.getNetworkHandler().sendPacket(new QueryPingC2SPacket(now));
            this.lastPing = now;
        }
    }

    @Override
    public void onRender(final DrawContext context, final float delta, final boolean inGame) {
        final Map<String, String> infoMap = new LinkedHashMap<>();

        if (this.fps.getValue()) {
            infoMap.put("FPS", Integer.toString(this.mc.getCurrentFps()));
        }

        if (this.cps.getValue()) {
            this.leftClick.update();
            this.rightClick.update();
            infoMap.put("CPS", this.leftClick.clicks() + " | " + this.rightClick.clicks());
        }

        if (this.username.getValue()) {
            String username = this.mc.session.getUsername();
            if (this.mc.player != null) {
                username = this.mc.player.getGameProfile().getName();
            }
            infoMap.put("Username", username);
        }

        final double posX, posY, posZ;
        if (this.mc.player != null) {
            posX = this.mc.player.getX();
            posY = this.mc.player.getY();
            posZ = this.mc.player.getZ();
        } else {
            posX = 0d;
            posY = 0d;
            posZ = 0d;
        }

        if (this.position.getValue()) {
            final int positionDecimalPlacesRawValue = this.positionDecimalPlaces.getValue();
            if (positionDecimalPlacesRawValue < 1) this.positionDecimalPlaces.setValue(1);
            else if (positionDecimalPlacesRawValue > 15) this.positionDecimalPlaces.setValue(15);
            final String positionDecimalPlaces = "%." + this.positionDecimalPlaces.getValue() + "f";
            infoMap.put(
                    "Position",
                    String.format(
                            positionDecimalPlaces + ", " +
                                    positionDecimalPlaces + ", " +
                                    positionDecimalPlaces,
                            posX, posY, posZ
                    )
            );
        }

        if (this.dimensionalPosition.getValue()) {
            final WorldUtil.Dimension dimension = this.mc.player == null ? WorldUtil.Dimension.OVERWORLD : WorldUtil.getDimension();
            if (dimension != WorldUtil.Dimension.END) {
                final int positionDecimalPlacesRawValue = this.positionDecimalPlaces.getValue();
                if (positionDecimalPlacesRawValue < 1) this.positionDecimalPlaces.setValue(1);
                else if (positionDecimalPlacesRawValue > 15) this.positionDecimalPlaces.setValue(15);
                final int decimalPlaces = this.positionDecimalPlaces.getValue();
                final String positionFormat = "%." + decimalPlaces + "f";
                String name = "";
                double correctedX = posX, correctedZ = posZ;
                switch (dimension) {
                    case NETHER -> {
                        name = "Overworld Position";
                        correctedX = posX * 8;
                        correctedZ = posZ * 8;
                    }
                    case OVERWORLD -> {
                        name = "Nether Position";
                        correctedX = posX / 8;
                        correctedZ = posZ / 8;
                    }
                    default -> {
                    }
                }
                infoMap.put(name, String.format(
                        positionFormat + ", " + positionFormat + ", " + positionFormat,
                        correctedX,
                        posY,
                        correctedZ
                ));
            }
        }

        if (this.difficulty.getValue()) {
            infoMap.put(
                    "Difficulty",
                    this.mc.world != null ? this.mc.world.getDifficulty().getName() : "unknown"
            );
        }

        if (this.permissionsLevel.getValue()) {
            infoMap.put(
                    "Permissions Level",
                    this.mc.player != null ? Integer.toString(this.mc.player.getPermissionLevel()) : "unknown"
            );
        }

        if (this.serverBrand.getValue()) {
            String value = "unknown";
            if (this.mc.getNetworkHandler() != null) {
                final String brand = this.mc.getNetworkHandler().getBrand();
                if (brand != null) {
                    value = brand.replaceAll("\\(.*?\\)", "");
                }
            }
            infoMap.put("Server Brand", value);
        }

        if (this.serverAddress.getValue()) {
            String value = "unknown";
            if (this.mc.player != null && !this.mc.isInSingleplayer()) {
                final ServerInfo currentServerInfo = ServerConnectionUtil.getLastServerInfo();
                if (currentServerInfo != null) {
                    value = currentServerInfo.address;
                }
            }
            infoMap.put("Server Address", value);
        }

        if (this.ping.getValue()) {
            String value = "unknown";

            if (this.clientPing > 3 && this.fasterPings.getValue()) {
                value = Long.toString(this.clientPing);
            } else {
                if (this.mc.getNetworkHandler() != null) {
                    final PlayerListEntry playerListEntry = this.mc.getNetworkHandler().getPlayerListEntry(this.mc.player.getGameProfile().getId());

                    if (playerListEntry != null) {
                        value = String.valueOf(playerListEntry.getLatency());
                    }
                }
            }

            infoMap.put("Ping", value + " ms");
        }

        if (this.packetsSent.getValue()) {
            String value = "unknown";
            if (this.mc.getNetworkHandler() != null) {
                value = String.format("%.0f", this.mc.getNetworkHandler().getConnection().getAveragePacketsSent());
            }
            infoMap.put("Packets Sent", value);
        }

        if (this.packetsReceived.getValue()) {
            String value = "unknown";
            if (this.mc.getNetworkHandler() != null) {
                value = String.format("%.0f", this.mc.getNetworkHandler().getConnection().getAveragePacketsReceived());
            }
            infoMap.put("Packets Received", value);
        }

        if (this.lagMeter.getValue()) {
            final long lagMillis = this.mc.getNetworkHandler() != null && !(this.mc.isInSingleplayer() && this.mc.isPaused()) ? System.currentTimeMillis() - this.lastUpdate : 0L;
            if (lagMillis > this.lagMeterThreshold.getValue() || !inGame) {
                infoMap.put("Lag", lagMillis + " ms");
            }
        }

        if (this.tickBaseCharge.getValue()) {
            final TickBaseModule tickBaseModule = Vandalism.getInstance().getModuleManager().getTickBaseModule();
            if (tickBaseModule.isActive() || !inGame) {
                infoMap.put("Tick Base Charge", (inGame ? tickBaseModule.getCharge() : 0) + " t");
            }
        }

        if (this.clientTPS.getValue()) {
            final float tps = ((IRenderTickCounter) this.mc.renderTickCounter).vandalism$getTPS();
            final float percentage = tps / 20.0f;

            infoMap.put("Client TPS", String.format("%.3f (%.3f)", tps, percentage));
        }

        int width = 0, height = 0;
        final int fontHeight = this.mc.textRenderer.fontHeight;
        for (final Map.Entry<String, String> infoEntry : infoMap.entrySet()) {
            if (this.alignmentX == Alignment.MIDDLE) {
                final String[] infoParts = new String[]{infoEntry.getKey(), infoEntry.getValue()};
                for (int i = 0; i < infoParts.length; i++) {
                    final String infoPart = infoParts[i];
                    final int textWidth = this.mc.textRenderer.getWidth(infoPart);
                    this.drawText(
                            context,
                            (i == 0 ? Formatting.UNDERLINE : "") + infoPart,
                            (this.x + this.width / 2) - textWidth / 2,
                            this.y + height
                    );
                    height += fontHeight + 3;
                    if (textWidth > width) {
                        width = textWidth;
                    }
                }
            } else {
                final String text;
                int textWidth = 0;
                switch (this.alignmentX) {
                    case LEFT -> {
                        text = infoEntry.getKey() + " » " + infoEntry.getValue();
                        textWidth = this.mc.textRenderer.getWidth(text);
                        this.drawText(context, text, this.x, this.y + height);
                        height += fontHeight;
                    }
                    case RIGHT -> {
                        text = infoEntry.getValue() + " « " + infoEntry.getKey();
                        textWidth = this.mc.textRenderer.getWidth(text);
                        this.drawText(context, text, (this.x + this.width) - textWidth, this.y + height);
                        height += fontHeight;
                    }
                }
                if (textWidth > width) {
                    width = textWidth;
                }
            }
        }
        this.width = width;
        this.height = height;
    }

    private void drawText(final DrawContext context, final String text, final int x, final int y) {
        context.drawText(
                this.mc.textRenderer,
                text,
                x,
                y,
                this.color.getColor(-y * 20).getRGB(),
                this.shadow.getValue()
        );
    }

}

