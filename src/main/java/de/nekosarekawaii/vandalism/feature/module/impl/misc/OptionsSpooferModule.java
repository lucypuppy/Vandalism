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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.StringValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.ModeValue;
import de.nekosarekawaii.vandalism.event.game.WorldListener;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.MSTimer;
import de.nekosarekawaii.vandalism.util.StringUtils;
import de.nekosarekawaii.vandalism.util.math.RandomUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.util.Arm;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class OptionsSpooferModule extends Module implements WorldListener, OutgoingPacketListener, PlayerUpdateListener {

    private static final SyncedClientOptions DEFAULT_OPTIONS = SyncedClientOptions.createDefault();

    private final StringValue language = new StringValue(
            this,
            "Language",
            "The game language.",
            DEFAULT_OPTIONS.language()
    ).onValueChange((oldValue, newValue) -> this.sendPacket());

    private final IntegerValue viewDistance = new IntegerValue(
            this,
            "View Distance",
            "The view distance.",
            DEFAULT_OPTIONS.viewDistance(),
            0,
            32
    ).onValueChange((oldValue, newValue) -> this.sendPacket());

    private final ModeValue chatVisibility = new ModeValue(
            this,
            "Chat Visibility",
            "The chat visibility.",
            Arrays.stream(ChatVisibility.values()).map(visibility -> StringUtils.normalizeEnumName(visibility.name())).toArray(String[]::new)
    ).onValueChange((oldValue, newValue) -> this.sendPacket());

    private final BooleanValue chatColorsEnabled = new BooleanValue(
            this,
            "Chat Colors Enabled",
            "Whether chat colors are enabled.",
            DEFAULT_OPTIONS.chatColorsEnabled()
    ).onValueChange((oldValue, newValue) -> this.sendPacket());

    private final IntegerValue playerModelParts = new IntegerValue(
            this,
            "Player Model Parts",
            "The player model parts.",
            DEFAULT_OPTIONS.playerModelParts(),
            0,
            255
    ).onValueChange((oldValue, newValue) -> this.sendPacket());

    private final ModeValue mainArm = new ModeValue(
            this,
            "Main Arm",
            "The main arm.",
            Arrays.stream(Arm.values()).map(arm -> StringUtils.normalizeEnumName(arm.name())).collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                Collections.reverse(list); // Reverse the list to make the default value right (idk why the first entry in the enum is left)
                return list.toArray(new String[0]);
            }))
    ).onValueChange((oldValue, newValue) -> this.sendPacket());

    private final BooleanValue filtersText = new BooleanValue(
            this,
            "Filters Text",
            "Whether text is filtered.",
            DEFAULT_OPTIONS.filtersText()
    ).onValueChange((oldValue, newValue) -> this.sendPacket());

    private final BooleanValue allowsServerListing = new BooleanValue(
            this,
            "Allows Server Listing",
            "Whether server listing is allowed.",
            true
    ).onValueChange((oldValue, newValue) -> this.sendPacket());

    private final IntegerValue minWorldLoadPacketDelay = new IntegerValue(
            this,
            "Min World Load Packet Delay",
            "The minimum delay between sending the options packet after the world has been loaded.",
            1000,
            100,
            10000
    );

    private final IntegerValue maxWorldLoadPacketDelay = new IntegerValue(
            this,
            "Max World Load Packet Delay",
            "The maximum delay between sending the options packet after the world has been loaded.",
            1500,
            150,
            10000
    );

    private final MSTimer worldLoadPacketDelayTimer = new MSTimer();
    private boolean worldHasBeenLoaded = false;

    public OptionsSpooferModule() {
        super("Options Spoofer", "Allows you to spoof server side settings.", Category.MISC);
    }

    private void sendPacket() {
        if (!this.isActive()) {
            return;
        }
        final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        if (networkHandler == null) {
            return;
        }
        networkHandler.sendPacket(new ClientOptionsC2SPacket(DEFAULT_OPTIONS));
    }

    @Override
    protected void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, OutgoingPacketEvent.ID, WorldLoadEvent.ID, PlayerUpdateEvent.ID);
        this.sendPacket();
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, OutgoingPacketEvent.ID, WorldLoadEvent.ID, PlayerUpdateEvent.ID);
        mc.options.sendClientSettings();
        this.worldHasBeenLoaded = false;
    }

    @Override
    public void onPostWorldLoad() {
        this.worldHasBeenLoaded = true;
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        // Using this to prevent an possible server side detection caused by the client sending the packet too early
        if (this.worldHasBeenLoaded) {
            if (this.worldLoadPacketDelayTimer.hasReached(RandomUtils.randomInt(this.minWorldLoadPacketDelay.getValue(), this.maxWorldLoadPacketDelay.getValue()), true)) {
                this.worldHasBeenLoaded = false;
                this.sendPacket();
            }
        }
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        // Using this to prevent the user from sending an options packet without the settings of this module
        if (event.packet instanceof final ClientOptionsC2SPacket packet) {
            packet.options = new SyncedClientOptions(
                    this.language.getValue(),
                    this.viewDistance.getValue(),
                    ChatVisibility.valueOf(this.chatVisibility.getValue().toUpperCase()),
                    this.chatColorsEnabled.getValue(),
                    this.playerModelParts.getValue(),
                    Arm.valueOf(this.mainArm.getValue().toUpperCase()),
                    this.filtersText.getValue(),
                    this.allowsServerListing.getValue()
            );
        }
    }

}
