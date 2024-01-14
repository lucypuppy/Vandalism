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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.normal.network.BlockCollisionShapeListener;
import de.nekosarekawaii.vandalism.base.value.impl.minecraft.MultiBlockValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.List;

public class BlockNormalizerModule extends AbstractModule implements BlockCollisionShapeListener {

    private static final List<Block> PRESET_BLOCKS = new ArrayList<>();

    static {
        PRESET_BLOCKS.add(Blocks.CACTUS);
        PRESET_BLOCKS.add(Blocks.SWEET_BERRY_BUSH);
        PRESET_BLOCKS.add(Blocks.POWDER_SNOW);
        PRESET_BLOCKS.add(Blocks.COBWEB);
        PRESET_BLOCKS.add(Blocks.POINTED_DRIPSTONE);
        PRESET_BLOCKS.add(Blocks.BIG_DRIPLEAF);
    }

    private final MultiBlockValue affectedBlocks = new MultiBlockValue(
            this,
            "Blocks",
            "Change the blocks that are affected by this module.",
            PRESET_BLOCKS,
            PRESET_BLOCKS.toArray(Block[]::new)
    );

    private final BooleanValue fluidBlocks = new BooleanValue(
            this,
            "Fluid Blocks",
            "Whether or not to apply this module to fluid blocks.",
            true
    );

    private final BooleanValue disableOnSneak = new BooleanValue(
            this,
            "Disable on Sneak",
            "Whether or not to disable this module when you are sneaking.",
            true
    );

    public BlockNormalizerModule() {
        super(
                "Block Normalizer",
                "Changes the collision shape of certain blocks to full block shapes.",
                Category.MOVEMENT
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(BlockCollisionShapeEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(BlockCollisionShapeEvent.ID, this);
    }

    @Override
    public void onBlockCollisionShape(final BlockCollisionShapeEvent event) {
        if (this.disableOnSneak.getValue() && this.mc.options.sneakKey.isPressed()) {
            return;
        }
        final BlockState state = event.state;
        final FluidState fluidState = state.getFluidState();
        final boolean isFluid = this.fluidBlocks.getValue() && event.pos.getY() < this.mc.player.getY() && !fluidState.isEmpty();
        if (this.affectedBlocks.isSelected(state.getBlock()) || isFluid) {
            final double minX = 0, minY = 0, minZ = 0, maxX = 1, maxZ = 1;
            double maxY = 1;
            if (isFluid && fluidState.getFluid() instanceof WaterFluid && (this.mc.player.isOnFire() || this.mc.player.fallDistance >= 2)) {
                maxY = 0.59;
            }
            event.shape = VoxelShapes.cuboid(
                    minX,
                    minY,
                    minZ,
                    maxX,
                    maxY,
                    maxZ
            );
        }
    }

}
