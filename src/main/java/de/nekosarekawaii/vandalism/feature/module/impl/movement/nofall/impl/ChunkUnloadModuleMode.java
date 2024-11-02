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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.nofall.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.game.WorldListener;
import de.nekosarekawaii.vandalism.event.player.HealthUpdateListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.nofall.NoFallModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.PacketHelper;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;

public class ChunkUnloadModuleMode extends ModuleMulti<NoFallModule> implements PlayerUpdateListener, HealthUpdateListener, WorldListener {

    private int groundTicks = 0;
    private WorldChunk unloadedChunk;
    private LightingProvider lightingProvider;
    private State state = State.NONE;

    public ChunkUnloadModuleMode() {
        super("Chunk Unload");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, HealthUpdateEvent.ID, WorldLoadEvent.ID);
        this.state = State.NONE;
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, HealthUpdateEvent.ID, WorldLoadEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        final int x = mc.player.getBlockPos().getX() >> 4;
        final int z = mc.player.getBlockPos().getZ() >> 4;

        if (this.state == State.NONE) {
            if (mc.player.fallDistance > 3.0) {
                this.unloadedChunk = mc.world.getChunkManager().getWorldChunk(x, z);
                this.lightingProvider = mc.world.getChunkManager().getLightingProvider();

                // Unload the chunk
                PacketHelper.receivePacket(new UnloadChunkS2CPacket(new ChunkPos(x, z)));

                this.state = State.UNLOADED_CHUNK;
            }
        } else if (this.state == State.UNLOADED_CHUNK) {
            if (!mc.world.getChunkManager().isChunkLoaded(x, z) && this.unloadedChunk != null) {
                final BlockState blockState = this.unloadedChunk.getBlockState(mc.player.getBlockPos().add(0, -1, 0));

                // Check if the player is on the ground.
                if (!blockState.isAir()) {
                    this.groundTicks++;
                }

                if (this.groundTicks > 3) {
                    //First try loading the chunk
                    PacketHelper.receivePacket(new ChunkDataS2CPacket(this.unloadedChunk, this.lightingProvider, null, null));
                    this.state = State.TRYING_TO_LOAD_CHUNK;
                    this.groundTicks = 0;
                } else if (this.groundTicks > 0) {
                    // Simulate stuck in block.
                    PacketHelper.sendImmediately(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() - 1, mc.player.getZ(), true), null, true);
                }
            }
        } else if (this.state == State.TRYING_TO_LOAD_CHUNK) {
            // Check if the chunk is really loaded.
            if (mc.world.getChunkManager().isChunkLoaded(x, z)) {
                this.unloadedChunk = null;
                this.lightingProvider = null;
                mc.player.fallDistance = 0.0F;
                this.state = State.CHUNK_LOADED;
            } else {
                // Try loading the chunk again.
                PacketHelper.receivePacket(new ChunkDataS2CPacket(this.unloadedChunk, this.lightingProvider, null, null));
            }
        } else if (this.state == State.CHUNK_LOADED) {
            // Fix anticheat flags.
            mc.player.setVelocity(0, -0.5016000115871435, 0);
            this.state = State.NONE;
        }

        //if (this.state != State.NONE)
        //    ChatUtil.infoChatMessage("State: " + this.state);
    }

    @Override
    public void onHealthUpdate(HealthUpdateEvent event) {
        if (event.health <= 0.0F) {
            this.state = State.NONE;
        }
    }

    @Override
    public void onPreWorldLoad() {
        this.state = State.NONE;
    }

    private enum State {
        NONE,
        UNLOADED_CHUNK,
        TRYING_TO_LOAD_CHUNK,
        CHUNK_LOADED
    }

}
