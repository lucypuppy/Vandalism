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
import de.nekosarekawaii.vandalism.base.value.impl.minecraft.MultiRegistryBlacklistValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import de.nekosarekawaii.vandalism.util.MSTimer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;

public class BlockBreakerModule extends Module implements PlayerUpdateListener, IncomingPacketListener {

    private final MultiRegistryBlacklistValue<Block> affectedBlocks = new MultiRegistryBlacklistValue<>(
            this,
            "Blocks",
            "Change the blocks that are affected by this module.",
            Registries.BLOCK,
            Arrays.asList(
                    Blocks.AIR,
                    Blocks.CAVE_AIR,
                    Blocks.VOID_AIR
            )
    );

    private final IntegerValue scanRange = new IntegerValue(
            this,
            "Scan Range",
            "The range where the block breaker breaks blocks.",
            4,
            3,
            10
    );

    private final BooleanValue checkBreak = new BooleanValue(
            this,
            "Check Break",
            "Check if the block is really broken.",
            true
    );

    private final IntegerValue checkTime = new IntegerValue(
            this,
            "Max Check Time",
            "The max time for checking if the block is really destroyed.",
            100,
            50,
            2000
    ).visibleCondition(this.checkBreak::getValue);

    private BlockPos validPos = null;
    private boolean checkBlockStatus = false;
    private final MSTimer checkTimer = new MSTimer();

    public BlockBreakerModule() {
        super(
                "Block Breaker",
                "Automatically breaks selected blocks around you.",
                Category.MISC
        );
        this.markExperimental();
    }

    @Override
    public void onActivate() {
        this.checkBlockStatus = false;
        this.validPos = null;
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, IncomingPacketEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, IncomingPacketEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.validPos == null) {
            final int scanRange = this.scanRange.getValue();

            double nearest = -1;
            for (int x = -scanRange; x < scanRange; x++) {
                for (int y = -scanRange; y < scanRange; y++) {
                    for (int z = -scanRange; z < scanRange; z++) {
                        final BlockPos pos = mc.player.getBlockPos().mutableCopy().add(x, y, z);
                        final BlockState blockState = mc.world.getBlockState(pos);

                        if (this.affectedBlocks.isSelected(blockState.getBlock())) {
                            final double distance = mc.player.getPos().distanceTo(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));

                            if (nearest == -1 || nearest > distance) {
                                this.validPos = pos;
                                nearest = distance;
                            }
                        }
                    }
                }
            }
        }

        // Sanity Check
        if (this.validPos == null) {
            return;
        }

        if (this.checkBlockStatus) {
            if (this.checkTimer.hasReached(this.checkTime.getValue())) {
                ChatUtil.infoChatMessage("Block successfully broken.");
                this.checkBlockStatus = false;
                this.validPos = null;
            }

            return;
        }

        final BlockState state = mc.world.getBlockState(this.validPos);
        final Direction direction = Direction.getFacing(this.validPos.getX(), this.validPos.getY(), this.validPos.getZ());

        if (direction == null) {
            return;
        }

        // Interact with the block to check if it's really broken.
        if (state.isAir()) {
            if (this.checkBreak.getValue()) {
                this.checkBlockStatus = true;
                this.checkTimer.reset();
                interactBlock(direction); // Interact for a block update
            } else {
                ChatUtil.infoChatMessage("Block successfully broken.");
                this.checkBlockStatus = false; // Reset check.
                this.validPos = null;
            }

            return;
        }

        breakBlock(direction);
    }

    @Override
    public void onIncomingPacket(IncomingPacketEvent event) {
        final Packet<?> packet = event.packet;

        if (!this.checkBlockStatus)
            return;

        if (packet instanceof final BlockUpdateS2CPacket blockUpdateS2CPacket) {
            final BlockPos pos = blockUpdateS2CPacket.getPos();
            final BlockState state = blockUpdateS2CPacket.getState();

            if (pos.equals(this.validPos) && this.affectedBlocks.isSelected(state.getBlock())) {

                ChatUtil.infoChatMessage("Failed to break block, block is protected.");
                this.checkBlockStatus = false;
                this.validPos = null;
            }
        }
    }

    private void breakBlock(final Direction direction) {
        if (mc.player.isCreative()) {
            mc.interactionManager.attackBlock(validPos, direction);
            return;
        }

        if (mc.interactionManager.updateBlockBreakingProgress(validPos, direction)) {
            mc.player.swingHand(Hand.MAIN_HAND);
            mc.particleManager.addBlockBreakingParticles(validPos, direction);
        }
    }

    private void interactBlock(final Direction direction) {
        final Vec3d pos = new Vec3d(this.validPos.getX(), this.validPos.getY(), this.validPos.getZ());
        final BlockHitResult blockHitResult = new BlockHitResult(pos, direction, this.validPos, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, blockHitResult);
    }

}
