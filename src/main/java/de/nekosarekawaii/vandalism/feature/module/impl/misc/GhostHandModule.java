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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.minecraft.MultiRegistryBlacklistValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.MSTimer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;

public class GhostHandModule extends AbstractModule implements PlayerUpdateListener {

    public final MultiRegistryBlacklistValue<Block> blockList = new MultiRegistryBlacklistValue<>(
            this,
            "Block List",
            "The blocks to target.",
            Registries.BLOCK,
            Arrays.asList(
                    Blocks.AIR,
                    Blocks.CAVE_AIR,
                    Blocks.VOID_AIR
            )
    );

    private final DoubleValue throughEntityReach = new DoubleValue(
            this,
            "Through Entity Reach",
            "The reach to interact with blocks through entities.",
            3.0,
            0.0,
            5.0
    );

    private final MSTimer interactionDelay = new MSTimer();

    public GhostHandModule() {
        super(
                "Ghost Hand",
                "Allows you to interact with blocks through walls.",
                Category.MISC
        );
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
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (!this.mc.options.useKey.isPressed() || !this.interactionDelay.hasReached(10, true)) return;
        final Entity cameraEntity = this.mc.getCameraEntity();
        final HitResult hitResult = cameraEntity.raycast(this.throughEntityReach.getValue(), 0, false);
        if (hitResult instanceof final BlockHitResult blockHitResult) {
            final BlockPos blockPos = blockHitResult.getBlockPos();
            final BlockEntity blockEntity = this.mc.world.getBlockEntity(blockPos);
            if (blockEntity != null) {
                this.mc.interactionManager.interactBlock(this.mc.player, Hand.MAIN_HAND, blockHitResult);
            }
        }
    }

}
