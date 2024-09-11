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

package de.nekosarekawaii.vandalism.feature.hud.impl;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.network.DisconnectListener;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.hud.HUDElement;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.TickBaseModule;
import de.nekosarekawaii.vandalism.injection.access.IRenderTickCounter;
import de.nekosarekawaii.vandalism.util.CPSTracker;
import de.nekosarekawaii.vandalism.util.DateUtil;
import de.nekosarekawaii.vandalism.util.ServerUtil;
import de.nekosarekawaii.vandalism.util.WorldUtil;
import de.nekosarekawaii.vandalism.util.render.Buffers;
import de.nekosarekawaii.vandalism.util.render.Shaders;
import de.nekosarekawaii.vandalism.util.render.gl.render.AttribConsumerProvider;
import de.nekosarekawaii.vandalism.util.render.gl.render.ImmediateRenderer;
import de.nekosarekawaii.vandalism.util.render.util.AlignmentX;
import de.nekosarekawaii.vandalism.util.render.util.AlignmentY;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.config.SelectKnownPacksS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import org.joml.Vector2f;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class InfoHUDElement extends HUDElement implements IncomingPacketListener, PlayerUpdateListener, DisconnectListener {

    private static final Pattern BRAND_PATTERN = Pattern.compile("\\(.*?\\)");

    private final BooleanValue shadow = new BooleanValue(
            this,
            "Shadow",
            "Whether or not the text should have a shadow.",
            true
    );

    private final ColorValue infoNameColor = new ColorValue(
            this,
            "Info Name Color",
            "The color of the info name."
    );

    private final ColorValue bracketColor = new ColorValue(
            this,
            "Bracket Color",
            "The color of the brackets.",
            Color.GRAY
    );

    private final ColorValue infoValueColor = new ColorValue(
            this,
            "Info Value Color",
            "The color of the info value.",
            Color.WHITE
    );

    private final BooleanValue glowOutline = new BooleanValue(
            this,
            "Glow Outline",
            "Activates/Deactivates the glow outline.",
            false
    );

    private final ColorValue glowOutlineColor = new ColorValue(
            this,
            "Glow Outline Color",
            "The color of the glow outline.",
            Color.lightGray
    ).visibleCondition(this.glowOutline::getValue);

    private final FloatValue glowOutlineWidth = new FloatValue(
            this,
            "Glow Outline Width",
            "The width of the glow outline.",
            6.0f,
            1.0f,
            20.0f
    ).visibleCondition(this.glowOutline::getValue);

    private final FloatValue glowOutlineAccuracy = new FloatValue(
            this,
            "Glow Outline Accuracy",
            "The accuracy of the glow outline.",
            1.0f,
            1.0f,
            8.0f
    ).visibleCondition(this.glowOutline::getValue);

    private final FloatValue glowOutlineExponent = new FloatValue(
            this,
            "Glow Outline Exponent",
            "The exponent of the glow outline.",
            0.22f,
            0.01f,
            4.0f
    ).visibleCondition(this.glowOutline::getValue);

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

    private final BooleanValue position = new BooleanValue(
            this,
            "Position",
            "Shows the current position.",
            true
    );

    private final BooleanValue dimensionalPosition = new BooleanValue(
            this,
            "Dimensional Position",
            "Shows the current position of the dimension you are currently playing in.",
            true
    );

    private final IntegerValue positionDecimalPlaces = new IntegerValue(
            this,
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

    private final BooleanValue entities = new BooleanValue(
            this,
            "Entities",
            "Shows the current amount of entities.",
            true
    );

    private final BooleanValue permissionsLevel = new BooleanValue(
            this,
            "Permissions Level",
            "Shows the current permissions level.",
            true
    );

    private final BooleanValue serverBrand = new BooleanValue(
            this,
            "Server Brand",
            "Shows the current server brand.",
            true
    );

    private final BooleanValue serverVersion = new BooleanValue(
            this,
            "Server Version",
            "Shows the current server version.",
            true
    );

    private final BooleanValue serverAddress = new BooleanValue(
            this,
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
            2000,
            500,
            10000
    ).visibleCondition(this.lagMeter::getValue);

    private final BooleanValue tickBaseCharge = new BooleanValue(
            this,
            "Tick Base Charge",
            "Shows the current tick base charge.",
            true
    );

    private final BooleanValue clientTPS = new BooleanValue(
            this,
            "Client TPS",
            "Shows the current client TPS.",
            false
    );

    public final CPSTracker leftClick = new CPSTracker();
    public final CPSTracker rightClick = new CPSTracker();

    private long lastUpdate = System.currentTimeMillis();
    private long lastPing = -1;
    private long clientPing = -1;

    private String serverVersionValue = "";

    public InfoHUDElement() {
        super("Info", true, AlignmentX.LEFT, AlignmentY.MIDDLE);
        Vandalism.getInstance().getEventSystem().subscribe(this, IncomingPacketEvent.ID, PlayerUpdateEvent.ID, DisconnectEvent.ID);
    }

    @Override
    public void onDisconnect(final ClientConnection clientConnection, final Text disconnectReason) {
        this.serverVersionValue = "";
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        final Packet<?> packet = event.packet;
        final long now = System.currentTimeMillis();

        if (packet instanceof KeepAliveS2CPacket || packet instanceof WorldTimeUpdateS2CPacket) {
            this.lastUpdate = now;
        }

        if (
                packet instanceof final PingResultS2CPacket pingResultS2CPacket &&
                this.ping.getValue() &&
                this.fasterPings.getValue() &&
                        !ProtocolTranslator.getTargetVersion().olderThan(ProtocolVersion.v1_20_2)
        ) {
            this.clientPing = now - pingResultS2CPacket.startTime();
        }

        if (packet instanceof final SelectKnownPacksS2CPacket selectKnownPacksS2CPacket) {
            for (final VersionedIdentifier knownPack : selectKnownPacksS2CPacket.knownPacks()) {
                if (knownPack.isVanilla() && knownPack.id().equals("core")) {
                    this.serverVersionValue = knownPack.version();
                    break;
                }
            }
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        final long now = System.currentTimeMillis();

        if (now - this.lastPing > this.pingInterval.getValue() &&
                this.fasterPings.getValue() &&
                this.ping.getValue() &&
                !ProtocolTranslator.getTargetVersion().olderThan(ProtocolVersion.v1_20_2)) {
            this.mc.getNetworkHandler().sendPacket(new QueryPingC2SPacket(now));
            this.lastPing = now;
        }
    }

    private void drawText(AttribConsumerProvider batch, final DrawContext context, final Text text, final int x, final int y, final boolean isPostProcessing) {
        if (this.glowOutline.getValue()) {
            context.fill(
                    x - 2,
                    y - 1,
                    x + this.getTextWidth(text) + 2,
                    y + 1 + this.getFontHeight(),
                    1677721600
            );
        }
        if (!isPostProcessing) {
            this.drawText(batch, text, context, x, y, this.glowOutline.getValue() || this.shadow.getValue(), this.infoNameColor.getColor(-y * 20).getRGB());
        }
    }

    private void drawInfo(final DrawContext context, final Map<String, String> infoMap, final boolean isPostProcessing) {
        try (final ImmediateRenderer renderer = new ImmediateRenderer(Buffers.getImmediateBufferPool())) {
            final float outlineWidth = this.glowOutlineWidth.getValue();
            final float outlineAccuracy = this.glowOutlineAccuracy.getValue();
            final float outlineExponent = this.glowOutlineExponent.getValue();
            final Color glowOutlineColor = this.glowOutlineColor.getColor();
            final Vector2f sizeVec = new Vector2f();

            if (isPostProcessing) {
                Shaders.getGlowOutlineEffect().configure(outlineWidth, outlineAccuracy, outlineExponent);
                Shaders.getGlowOutlineEffect().bindMask();
            }

            int width = 0, height = 0;

            for (final Map.Entry<String, String> infoEntry : infoMap.entrySet()) {
                if (this.alignmentX.getValue() == AlignmentX.MIDDLE) {
                    final Text[] infoParts = new Text[]{Text.literal(infoEntry.getKey()), Text.literal(infoEntry.getValue())};
                    for (int i = 0; i < infoParts.length; i++) {
                        final Text infoPart = infoParts[i];
                        this.getTextSize(infoPart, sizeVec);
                        final int textWidth = (int) sizeVec.x;
                        final int textHeight = (int) sizeVec.y;
                        final Text text = i == 0 ? infoPart : Text.empty().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(this.infoValueColor.getColor().getRGB()))).append(infoPart);
                        this.drawText(renderer, context, text, this.getX() - textWidth / 2, this.getY() + height, isPostProcessing);
                        height += textHeight + 2;
                        if (textWidth > width) {
                            width = textWidth;
                        }
                    }
                } else {
                    final Text text;
                    int textWidth = 0;
                    switch (this.alignmentX.getValue()) {
                        case LEFT -> {
                            text = Text.empty()
                                    .append(Text.literal(infoEntry.getKey()).withColor(this.infoNameColor.getColor().getRGB()))
                                    .append(Text.literal(" » ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(this.bracketColor.getColor().getRGB()))))
                                    .append(infoEntry.getValue()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(this.infoValueColor.getColor().getRGB())));
                            this.getTextSize(text, sizeVec);
                            textWidth = (int) sizeVec.x;
                            this.drawText(renderer, context, text, this.getX(), this.getY() + height, isPostProcessing);
                            height += (int) (sizeVec.y + 2);
                        }
                        case RIGHT -> {
                            text = Text.empty()
                                    .append(Text.literal(infoEntry.getKey()).withColor(this.infoNameColor.getColor().getRGB()))
                                    .append(Text.literal(" « ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(this.bracketColor.getColor().getRGB()))))
                                    .append(infoEntry.getValue()).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(this.infoValueColor.getColor().getRGB())));
                            this.getTextSize(text, sizeVec);
                            textWidth = (int) sizeVec.x;
                            this.drawText(renderer, context, text, this.getX() - textWidth, this.getY() + height, isPostProcessing);
                            height += (int) (sizeVec.y + 2);
                        }
                    }
                    if (textWidth > width) {
                        width = textWidth;
                    }
                }
            }

            renderer.draw();

            if (isPostProcessing) {
                Shaders.getGlowOutlineEffect().renderFullscreen(Shaders.getColorFillEffect().maskFramebuffer().get(), false);
                Shaders.getColorFillEffect().setColor(glowOutlineColor);
                Shaders.getColorFillEffect().renderFullscreen(this.mc.getFramebuffer(), false);
            }

            this.width = width;
            this.height = height;
        }
    }

    @Override
    protected void onRender(final DrawContext context, final float delta, final boolean inGame) {
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

        if (this.position.getValue() && this.mc.player != null) {
            final double posX = this.mc.player.getX();
            final double posY = this.mc.player.getY();
            final double posZ = this.mc.player.getZ();
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

        if (this.dimensionalPosition.getValue() && this.mc.world != null && this.mc.player != null) {
            final double posX = this.mc.player.getX();
            final double posY = this.mc.player.getY();
            final double posZ = this.mc.player.getZ();
            final DimensionType dimensionType = this.mc.world.getDimension();
            if (dimensionType != WorldUtil.uncoverDimensionType(DimensionTypes.THE_END)) {
                final int positionDecimalPlacesRawValue = this.positionDecimalPlaces.getValue();
                if (positionDecimalPlacesRawValue < 1) this.positionDecimalPlaces.setValue(1);
                else if (positionDecimalPlacesRawValue > 15) this.positionDecimalPlaces.setValue(15);
                final int decimalPlaces = this.positionDecimalPlaces.getValue();
                final String positionFormat = "%." + decimalPlaces + "f";
                String name;
                double correctedX, correctedZ;
                if (dimensionType == WorldUtil.uncoverDimensionType(DimensionTypes.THE_NETHER)) {
                    name = "Overworld Position";
                    correctedX = posX * DimensionType.field_31440;
                    correctedZ = posZ * DimensionType.field_31440;
                } else {
                    name = "Nether Position";
                    correctedX = posX / DimensionType.field_31440;
                    correctedZ = posZ / DimensionType.field_31440;
                }
                infoMap.put(name, String.format(
                        positionFormat + ", " + positionFormat + ", " + positionFormat,
                        correctedX,
                        posY,
                        correctedZ
                ));
            }
        }

        if (this.entities.getValue() && this.mc.world != null) {
            infoMap.put("Entities", String.valueOf(this.mc.world.getRegularEntityCount()));
        }

        if (this.difficulty.getValue() && this.mc.world != null) {
            infoMap.put(
                    "Difficulty",
                    this.mc.world.getDifficulty().getName()
            );
        }

        if (this.permissionsLevel.getValue() && this.mc.player != null) {
            final int permissionLevel = this.mc.player.getPermissionLevel();
            if (permissionLevel != 0) {
                infoMap.put(
                        "Permissions Level",
                        String.valueOf(permissionLevel)
                );
            }
        }

        if (this.serverBrand.getValue() && this.mc.getNetworkHandler() != null) {
            String brand = this.mc.getNetworkHandler().getBrand();
            if (brand != null) {
                brand = BRAND_PATTERN.matcher(brand).replaceAll("");
                infoMap.put("Server " + (DateUtil.isAprilFools() ? "Engine" : "Brand"), brand);
            }
        }

        if (this.serverVersion.getValue() && this.mc.getNetworkHandler() != null) {
            if (this.serverVersionValue != null && !this.serverVersionValue.isEmpty()) {
                infoMap.put("Server Version", this.serverVersionValue);
            }
        }

        if (this.serverAddress.getValue() && this.mc.player != null && !this.mc.isInSingleplayer()) {
            final ServerInfo currentServerInfo = ServerUtil.getLastServerInfo();
            if (currentServerInfo != null) {
                infoMap.put("Server Address", currentServerInfo.address);
            }
        }

        if (this.ping.getValue() && this.mc.getNetworkHandler() != null) {
            int ping = 0;
            if (this.clientPing > 3 && this.fasterPings.getValue()) {
                ping = (int) this.clientPing;
            } else {
                final PlayerListEntry playerListEntry = this.mc.getNetworkHandler().getPlayerListEntry(this.mc.player.getGameProfile().getId());
                if (playerListEntry != null) {
                    ping = playerListEntry.getLatency();
                }
            }
            if (ping > 0) {
                infoMap.put("Ping", ping + " ms");
            }
        }

        if (this.packetsSent.getValue() && this.mc.getNetworkHandler() != null) {
            infoMap.put("Packets Sent", String.format("%.0f", this.mc.getNetworkHandler().getConnection().getAveragePacketsSent()));
        }

        if (this.packetsReceived.getValue() && this.mc.getNetworkHandler() != null) {
            infoMap.put("Packets Received", String.format("%.0f", this.mc.getNetworkHandler().getConnection().getAveragePacketsReceived()));
        }

        if (this.lagMeter.getValue()) {
            final long lagMillis = this.mc.getNetworkHandler() != null && !(this.mc.isInSingleplayer() && this.mc.isPaused()) ? System.currentTimeMillis() - this.lastUpdate : 0L;
            if (lagMillis > this.lagMeterThreshold.getValue()) {
                infoMap.put("Lag", lagMillis + " ms");
            }
        }

        if (this.tickBaseCharge.getValue()) {
            final TickBaseModule tickBaseModule = Vandalism.getInstance().getModuleManager().getTickBaseModule();
            final int charge = tickBaseModule.isActive() && inGame ? tickBaseModule.getCharge() : 0;
            if (charge > 0) {
                infoMap.put("Tick Base Charge", charge + " t");
            }
        }

        if (this.clientTPS.getValue()) {
            final float tps = ((IRenderTickCounter) this.mc.getRenderTickCounter()).vandalism$getTPS();
            final float percentage = tps / 20.0f;
            infoMap.put("Client TPS", String.format("%.3f (%.3f)", tps, percentage));
        }

        if (this.glowOutline.getValue()) {
            this.drawInfo(context, infoMap, true);
        }
        this.drawInfo(context, infoMap, false);
    }

}

