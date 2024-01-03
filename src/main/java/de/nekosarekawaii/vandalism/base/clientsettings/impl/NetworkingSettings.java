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

package de.nekosarekawaii.vandalism.base.clientsettings.impl;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.ClientSettings;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.StringValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.text.Text;

public class NetworkingSettings extends ValueGroup {

    public final BooleanValue changeBrand = new BooleanValue(
            this,
            "Change Brand",
            "Changes the Brand when connecting to a Server.",
            true
    );

    public final StringValue brand = new StringValue(
            this,
            "Brand",
            "The Brand that will used.",
            ClientBrandRetriever.VANILLA
    ).visibleCondition(this.changeBrand::getValue);

    public final BooleanValue forceDisconnectKeybind = new BooleanValue(
            this,
            "Force Disconnect Keybind",
            "Enables that you can disconnect with the key END even if the Game is frozen.",
            true
    );

    public final BooleanValue spoofIsCreativeLevelTwoOp = new BooleanValue(
            this,
            "Spoof Is Creative Level Two Op",
            "Makes the Game think you are a in Creative Mode and you have Level Two Op.",
            true
    );

    public final BooleanValue antiTelemetry = new BooleanValue(
            this,
            "Anti Telemetry",
            "Blocks the Telemetry of the Game.",
            true
    );

    public final BooleanValue antiServerBlockList = new BooleanValue(
            this,
            "Anti Server Block List",
            "Blocks the Server Block List from the Game.",
            true
    );

    public final BooleanValue antiTimeoutKick = new BooleanValue(
            this,
            "Anti Timeout Kick",
            "Prevents the Game from disconnecting if the server doesn't response for some time.",
            true
    );

    public final BooleanValue eliminateHitDelay = new BooleanValue(
            this,
            "Eliminate Hit Delay",
            "Eliminates the Hit Delay of the Game.",
            false
    );

    public NetworkingSettings(final ClientSettings parent) {
        super(parent, "Networking", "Networking related settings.");
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new NativeKeyListener() {

                @Override
                public void nativeKeyPressed(final NativeKeyEvent nativeEvent) {
                    if (Vandalism.getInstance().getClientSettings().getNetworkingSettings().forceDisconnectKeybind.getValue()) {
                        if (nativeEvent.getKeyCode() == 3663) {
                            if (NetworkingSettings.this.mc.getNetworkHandler() != null) {
                                NetworkingSettings.this.mc.getNetworkHandler().getConnection().disconnect(Text.literal("Manual force disconnect."));
                            }
                        }
                    }
                }

            });
        } catch (NativeHookException e) {
            Vandalism.getInstance().getLogger().error("Failed to register native input hook disconnect listener.", e);
        }
    }

}
