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

package de.nekosarekawaii.vandalism.util.astar;

import net.minecraft.util.math.BlockPos;

import java.util.*;

public class Pathfinder {

    public List<PathNode> findPath(final PathNode start, final PathNode goal) {
        if (start.equals(goal)) {
            return Collections.singletonList(start);
        }

        final PriorityQueue<PathNode> openSetForward = new PriorityQueue<>(Comparator.comparingDouble(PathNode::getFCost));
        final PriorityQueue<PathNode> openSetBackward = new PriorityQueue<>(Comparator.comparingDouble(PathNode::getFCost));

        final Map<BlockPos, PathNode> closedSetForward = new HashMap<>();
        final Map<BlockPos, PathNode> closedSetBackward = new HashMap<>();

        openSetForward.add(start);
        openSetBackward.add(goal);

        start.setGCost(0);
        start.setHCost(start.getHeuristic(goal));

        goal.setGCost(0);
        goal.setHCost(goal.getHeuristic(start));

        while (!openSetForward.isEmpty() && !openSetBackward.isEmpty()) {
            final PathNode currentForward = openSetForward.poll();
            final PathNode currentBackward = openSetBackward.poll();

            // Check if the forward and backward searches meet
            if (closedSetBackward.containsKey(currentForward.getPos())) {
                return reconstructPath(currentForward, closedSetBackward.get(currentForward.getPos()));
            }
            if (closedSetForward.containsKey(currentBackward.getPos())) {
                return reconstructPath(closedSetForward.get(currentBackward.getPos()), currentBackward);
            }

            closedSetForward.put(currentForward.getPos(), currentForward);
            closedSetBackward.put(currentBackward.getPos(), currentBackward);

            // Expand forward neighbors
            currentForward.generateNeighbours();
            for (final PathNode neighbor : currentForward.getNeighbors()) {
                if (closedSetForward.containsKey(neighbor.getPos()))
                    continue;

                final double tentativeGCost = currentForward.getGCost() + currentForward.distanceTo(neighbor);
                if (tentativeGCost < neighbor.getGCost() || !openSetForward.contains(neighbor)) {
                    neighbor.setGCost(tentativeGCost);
                    neighbor.setHCost(neighbor.getHeuristic(goal));
                    neighbor.setParent(currentForward);

                    openSetForward.add(neighbor);
                }
            }

            // Expand backward neighbors
            currentBackward.generateNeighbours();
            for (final PathNode neighbor : currentBackward.getNeighbors()) {
                if (closedSetBackward.containsKey(neighbor.getPos()))
                    continue;

                final double tentativeGCost = currentBackward.getGCost() + currentBackward.distanceTo(neighbor);
                if (tentativeGCost < neighbor.getGCost() || !openSetBackward.contains(neighbor)) {
                    neighbor.setGCost(tentativeGCost);
                    neighbor.setHCost(neighbor.getHeuristic(start));
                    neighbor.setParent(currentBackward);

                    openSetBackward.add(neighbor);
                }
            }
        }

        return Collections.emptyList(); // No path found
    }

    private List<PathNode> reconstructPath(PathNode meetingNodeForward, PathNode meetingNodeBackward) {
        final List<PathNode> path = new ArrayList<>();

        PathNode current = meetingNodeForward;
        while (current != null) {
            path.add(current);
            current = current.getParent();
        }
        Collections.reverse(path); // Reverse the forward part

        current = meetingNodeBackward.getParent(); // Skip the meeting node itself, already included
        while (current != null) {
            path.add(current);
            current = current.getParent();
        }

        return path;
    }

}