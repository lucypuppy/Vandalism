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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.florianmichael.rclasses.math.integration.MSTimer;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.concurrent.CopyOnWriteArrayList;

public class InteractionSpammerModule extends AbstractModule implements TickGameListener {

    private final IntegerValue maxXReach = new IntegerValue(
            this,
            "Max X Reach",
            "The max y reach.",
            3,
            0,
            5
    );

    private final IntegerValue maxZReach = new IntegerValue(
            this,
            "Max Z Reach",
            "The max z reach.",
            3,
            0,
            5
    );

    private final IntegerValue maxYReach = new IntegerValue(
            this,
            "Max Y Reach",
            "The max y reach.",
            3,
            0,
            5
    );

    private final IntegerValue interactionListsDelay = new IntegerValue(
            this,
            "Interaction Lists Delay",
            "The delay between interaction lists.",
            1000,
            0,
            2000
    );

    private final IntegerValue interactionDelay = new IntegerValue(
            this,
            "Interaction Delay",
            "The delay between interactions.",
            100,
            0,
            2000
    );

    private final CopyOnWriteArrayList<CopyOnWriteArrayList<BlockHitResult>> queue;

    private final MSTimer interactionListsTimer, interactionTimer;

    private CopyOnWriteArrayList<BlockHitResult> blockHitResults;

    public InteractionSpammerModule() {
        super("Interaction Spammer", "Lets you spam interactions.", Category.MISC);
        this.queue = new CopyOnWriteArrayList<>();
        this.interactionListsTimer = new MSTimer();
        this.interactionTimer = new MSTimer();
        this.blockHitResults = new CopyOnWriteArrayList<>();
    }

    private void clear() {
        this.queue.clear();
        this.blockHitResults.clear();
    }

    @Override
    public void onActivate() {
        this.clear();
        Vandalism.getInstance().getEventSystem().subscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(TickGameEvent.ID, this);
        this.clear();
    }

    @Override
    public void onTick() {
        final Entity cameraEntity = this.mc.getCameraEntity();
        if (this.mc.player == null || this.mc.world == null || this.mc.interactionManager == null || cameraEntity == null) {
            this.clear();
            return;
        }

        if (this.blockHitResults.isEmpty()) {
            if (!this.queue.isEmpty()) {
                if (this.interactionListsTimer.hasReached(this.interactionListsDelay.getValue(), true)) {
                    this.blockHitResults = this.queue.get(0);
                    this.queue.remove(this.blockHitResults);
                }
            }
        } else {
            for (final BlockHitResult blockHitResult : this.blockHitResults) {
                if (this.interactionTimer.hasReached(this.interactionDelay.getValue(), true)) {
                    this.mc.interactionManager.interactBlock(this.mc.player, Hand.MAIN_HAND, blockHitResult);
                    this.blockHitResults.remove(blockHitResult);
                }
            }
        }

        final HitResult hitResult = cameraEntity.raycast(this.mc.interactionManager.getReachDistance(), 0, false);
        if (!(hitResult instanceof final BlockHitResult blockHitResult)) return;

        final Block block = this.mc.world.getBlockState(blockHitResult.getBlockPos()).getBlock();
        if (!(block instanceof AirBlock || block instanceof FluidBlock)) {
            if (this.mc.options.useKey.isPressed()) {
                this.interactionListsTimer.reset();
                this.interactionTimer.reset();

                final CopyOnWriteArrayList<BlockHitResult> blockHitResults = new CopyOnWriteArrayList<>();
                for (int y = 0; y < this.maxYReach.getValue(); y++) {
                    for (int x = 0; x < this.maxXReach.getValue(); x++) {
                        for (int z = 0; z < this.maxZReach.getValue(); z++) {
                            blockHitResults.add(new BlockHitResult(
                                    blockHitResult.getPos().add(-x, y, z),
                                    blockHitResult.getSide(),
                                    blockHitResult.getBlockPos().add(-x, y, z),
                                    blockHitResult.isInsideBlock()
                            ));

                            blockHitResults.add(new BlockHitResult(
                                    blockHitResult.getPos().add(x, y, -z),
                                    blockHitResult.getSide(),
                                    blockHitResult.getBlockPos().add(x, y, -z),
                                    blockHitResult.isInsideBlock()
                            ));

                            blockHitResults.add(new BlockHitResult(
                                    blockHitResult.getPos().add(x, y, z),
                                    blockHitResult.getSide(),
                                    blockHitResult.getBlockPos().add(x, y, z),
                                    blockHitResult.isInsideBlock()
                            ));

                            blockHitResults.add(new BlockHitResult(
                                    blockHitResult.getPos().add(-x, y, -z),
                                    blockHitResult.getSide(),
                                    blockHitResult.getBlockPos().add(-x, y, -z),
                                    blockHitResult.isInsideBlock()
                            ));
                        }
                    }
                }

                this.queue.add(blockHitResults);
            }
        }
    }

}
