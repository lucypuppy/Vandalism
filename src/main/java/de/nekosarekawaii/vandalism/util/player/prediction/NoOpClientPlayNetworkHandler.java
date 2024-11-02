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

package de.nekosarekawaii.vandalism.util.player.prediction;

import com.mojang.authlib.GameProfile;
import de.florianmichael.asmfabricloader.api.AsmUtil;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.resource.featuretoggle.FeatureSet;

/**
 * Implementation of an empty client play network handler. This is used to create a fake network handler instance.
 * See {@link NoOpClientPlayerEntity} and {@link PredictionSystem} for more information.
 */
public class NoOpClientPlayNetworkHandler extends ClientPlayNetworkHandler implements MinecraftWrapper {

    /**
     * Creates a new fake ClientPlayNetworkHandler instance.
     *
     * @return The new instance or null if an error occurred.
     */
    public static NoOpClientPlayNetworkHandler get() {
        try {
            return (NoOpClientPlayNetworkHandler) AsmUtil.getTheUnsafe().allocateInstance(NoOpClientPlayNetworkHandler.class);
        } catch (InstantiationException e) {
            return null;
        }
    }

    private NoOpClientPlayNetworkHandler() { // We can't use this constructor as it would initialize the fields in super()
        super(null, null, null);
    }

    @Override
    public void sendPacket(Packet<?> packet) {
        // No op - we don't want to send packets
    }

    @Override
    public GameProfile getProfile() {
        return mc.getNetworkHandler().getProfile();
    }

    @Override
    public FeatureSet getEnabledFeatures() {
        return mc.getNetworkHandler().getEnabledFeatures();
    }

}
