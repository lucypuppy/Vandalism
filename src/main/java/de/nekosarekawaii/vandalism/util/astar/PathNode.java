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

package de.nekosarekawaii.vandalism.util.astar;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PathNode {

    private final World world;
    private final BlockPos pos;
    private final List<PathNode> neighbors;
    private Chunk chunk;

    @Setter
    private PathNode parent;

    @Setter
    private double gCost;

    @Setter
    private double hCost;

    private static final int[][] DIRECTIONS = {
            {1, 0, 0}, {-1, 0, 0},
            {0, 1, 0}, {0, -1, 0},
            {0, 0, 1}, {0, 0, -1},
            {1, 1, 0}, {1, -1, 0}, {-1, 1, 0}, {-1, -1, 0},
            {1, 0, 1}, {1, 0, -1}, {-1, 0, 1}, {-1, 0, -1},
            {0, 1, 1}, {0, 1, -1}, {0, -1, 1}, {0, -1, -1}
    };

    public PathNode(final World world, final BlockPos pos) {
        this.world = world;
        this.pos = pos;
        this.neighbors = new ArrayList<>();

        if (this.world.isChunkLoaded(
                ChunkSectionPos.getSectionCoord(pos.getX()),
                ChunkSectionPos.getSectionCoord(pos.getZ()))) {
            this.chunk = this.world.getChunk(pos);
        }
    }

    public void generateNeighbours() {
        for (final int[] direction : DIRECTIONS) {
            final BlockPos feetPos = this.pos.add(direction[0], direction[1], direction[2]);
            final PathNode node = new PathNode(this.world, feetPos);

            if (node.getChunk() == null) {
                continue;
            }

            final BlockPos headPos = feetPos.up();
            if (node.getChunk().getBlockState(feetPos).isAir() && node.getChunk().getBlockState(headPos).isAir()) {
                this.neighbors.add(node);
            }
        }
    }

    public double getFCost() {
        return this.gCost + this.hCost;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final PathNode node = (PathNode) obj;
        return this.pos.equals(node.pos);
    }

    public double distanceTo(final PathNode node) {
        return Math.sqrt(this.pos.getSquaredDistance(node.pos));
    }

    public double getManhattanDistance(final PathNode node) {
        return Math.abs(this.pos.getX() - node.pos.getX())
                + Math.abs(this.pos.getY() - node.pos.getY())
                + Math.abs(this.pos.getZ() - node.pos.getZ());
    }

    public double getHeuristic(final PathNode node) {
        return getManhattanDistance(node); // Todo: Add heuristic
    }

    public double getMovementCost(final PathNode node) {
        return getManhattanDistance(node); // Todo: Add movement cost
    }

}