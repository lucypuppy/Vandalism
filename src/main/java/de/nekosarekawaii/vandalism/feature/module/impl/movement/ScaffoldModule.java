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
import de.nekosarekawaii.vandalism.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.event.normal.player.RotationListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.integration.rotation.Rotation;
import de.nekosarekawaii.vandalism.integration.rotation.enums.RotationPriority;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ScaffoldModule extends AbstractModule implements PlayerUpdateListener, RotationListener {
    private BlockPos pos = null;

    public ScaffoldModule() {
        super("Scaffold", "Places blocks underneath you.", Category.MOVEMENT);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, RotationEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, RotationEvent.ID);
        Vandalism.getInstance().getRotationListener().resetRotation();
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        this.pos = getPlaceBlock(4);
    }

    @Override
    public void onPostPlayerUpdate(PlayerUpdateEvent event) {

    }

    @Override
    public void onRotation(RotationEvent event) {
        if (pos != null) {
            Rotation rotation = Rotation.Builder.build(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), mc.player.getEyePos());
            Vandalism.getInstance().getRotationListener().setRotation(rotation, RotationPriority.HIGH, 80f, 0, false);

        }
    }

    private BlockPos getPlaceBlock(int scanRange) {
        double distance = -1;
        BlockPos theChosenOne = null;

        for (int x = -scanRange; x < scanRange; x++) {
            for (int y = -scanRange; y < scanRange; y++) {
                for (int z = -scanRange; z < scanRange; z++) {
                    BlockPos pos = mc.player.getBlockPos().add(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    Block block = state.getBlock();
                    pos.offset(Direction.EAST);
                    if (state.isSolidBlock(mc.world, pos)) {
                        double currentDistance = mc.player.getBlockPos().getSquaredDistance(pos);
                        if (distance == -1 || currentDistance < distance) {
                            distance = currentDistance;
                            theChosenOne = pos;
                        }
                    }
                }
            }
        }
        ChatUtil.chatMessage(theChosenOne + "");
        return theChosenOne;
    }
}
