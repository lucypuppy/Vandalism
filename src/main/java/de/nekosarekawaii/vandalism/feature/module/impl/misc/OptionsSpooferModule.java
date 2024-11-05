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
import de.nekosarekawaii.vandalism.base.value.impl.selection.ModeValue;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.StringUtils;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.util.Arm;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class OptionsSpooferModule extends Module implements OutgoingPacketListener {

    private static final SyncedClientOptions DEFAULT_OPTIONS = SyncedClientOptions.createDefault();

    private final ModeValue language;

    private final IntegerValue viewDistance = new IntegerValue(
            this,
            "View Distance",
            "The view distance.",
            DEFAULT_OPTIONS.viewDistance(),
            0,
            32
    );

    private final ModeValue chatVisibility = new ModeValue(
            this,
            "Chat Visibility",
            "The chat visibility.",
            Arrays.stream(ChatVisibility.values()).map(visibility -> StringUtils.normalizeEnumName(visibility.name())).toArray(String[]::new)
    );

    private final BooleanValue chatColorsEnabled = new BooleanValue(
            this,
            "Chat Colors Enabled",
            "Whether chat colors are enabled.",
            DEFAULT_OPTIONS.chatColorsEnabled()
    );

    private final IntegerValue playerModelParts = new IntegerValue(
            this,
            "Player Model Parts",
            "The player model parts.",
            DEFAULT_OPTIONS.playerModelParts(),
            0,
            255
    );

    private final ModeValue mainArm = new ModeValue(
            this,
            "Main Arm",
            "The main arm.",
            Arrays.stream(Arm.values()).map(arm -> StringUtils.normalizeEnumName(arm.name())).collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                Collections.reverse(list); // Reverse the list to make the default value right (idk why the first entry in the enum is left)
                return list.toArray(new String[0]);
            }))
    );

    private final BooleanValue filtersText = new BooleanValue(
            this,
            "Filters Text",
            "Whether text is filtered.",
            DEFAULT_OPTIONS.filtersText()
    );

    private final BooleanValue allowsServerListing = new BooleanValue(
            this,
            "Allows Server Listing",
            "Whether server listing is allowed.",
            DEFAULT_OPTIONS.allowsServerListing()
    );

    public OptionsSpooferModule() {
        super("Options Spoofer", "Allows you to spoof server side settings.", Category.MISC);
        this.language = new ModeValue(
                this,
                "Language",
                "The game language.",
                mc.getLanguageManager().getAllLanguages().values().stream().map(LanguageDefinition::region).toArray(String[]::new)
        );
    }

    @Override
    protected void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, OutgoingPacketEvent.ID);
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, OutgoingPacketEvent.ID);
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
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
