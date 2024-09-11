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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.nofall.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.nofall.NoFallModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.PacketHelper;
import de.nekosarekawaii.vandalism.util.WorldUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;

public class PositionSpoofModuleMode extends ModuleMulti<NoFallModule> implements PlayerUpdateListener {

    private double groundSubtractor = -1.0, lastY = -1.0;
    private int groundTicks = 0;
    private WorldChunk unloadedChunk;
    private LightingProvider lightingProvider;

    public PositionSpoofModuleMode() {
        super("Position Spoof");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        final int x = mc.player.getBlockPos().getX() >> 4;
        final int z = mc.player.getBlockPos().getZ() >> 4;
        final boolean loaded = mc.world.getChunkManager().isChunkLoaded(x, z);

        if (!loaded) {
            if (this.unloadedChunk != null) {
                final double nextGround = this.lastY - this.groundSubtractor;
                final double yDiff = Math.abs(mc.player.getY() - nextGround);

                if (yDiff < 0.1) {
                    this.groundTicks++;
                } else {
                    this.groundTicks = 0;
                }

                if (this.groundTicks > 20) {
                    PacketHelper.receivePacket(new ChunkDataS2CPacket(this.unloadedChunk, this.lightingProvider, null, null));

                    this.unloadedChunk = null;
                    this.lightingProvider = null;
                    this.mc.player.fallDistance = 0.0F;
                } else if (this.groundTicks > 0) {
                    PacketHelper.sendImmediately(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() - 100, mc.player.getZ(), true), null, true);
                }
            }

            return;
        }

        if (this.groundTicks > 0) {
            this.groundTicks = 0;
            mc.player.jump();
            PacketHelper.sendImmediately(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() - 100, mc.player.getZ(), true), null, false);
            return;
        }

        if (mc.player.fallDistance > 3.0) {
            // Get the next ground block
            this.groundSubtractor = getNextGroundBlock();
            this.lastY = mc.player.getY();

            // Save the chunk and lighting provider for later
            this.unloadedChunk = mc.world.getChunkManager().getWorldChunk(x, z);
            this.lightingProvider = mc.world.getChunkManager().getLightingProvider();

            // Unload the chunk
            PacketHelper.receivePacket(new UnloadChunkS2CPacket(new ChunkPos(x, z)));
        }
    }

    private double getNextGroundBlock() {
        for (double i = 0; i < 255; i += 0.1) {
            if (WorldUtil.isOnGround(i)) {
                return i;
            }
        }

        return -1.0;
    }

}
