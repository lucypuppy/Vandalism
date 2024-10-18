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

import lombok.Getter;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class AsyncPathfinder {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Pathfinder pathfinder = new Pathfinder();

    @Getter
    private List<PathNode> lastPath;

    public void findPath(final PathNode start, final PathNode goal, @Nullable final Consumer<List<PathNode>> finish) {
        executorService.execute(() -> {
            lastPath = pathfinder.findPath(start, goal);

            if (finish != null)
                finish.accept(lastPath);
        });
    }

}
