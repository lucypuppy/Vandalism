/*
 * Copyright (C) 2021-2024 Verschlxfene
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
