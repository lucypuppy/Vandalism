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

package de.nekosarekawaii.vandalism.util.render.gl.render;

import de.nekosarekawaii.vandalism.util.render.gl.mem.ByteBufferBuilder;

public class IndirectCommands {

    public static int writeDrawArraysIndirect(ByteBufferBuilder dest, int count, int instanceCount, int firstVertex, int baseInstance) {
        dest.putInt(count);
        dest.putInt(instanceCount);
        dest.putInt(firstVertex);
        dest.putInt(baseInstance);
        return 16;
    }

    public static int writeDrawElementsIndirect(ByteBufferBuilder dest, int count, int instanceCount, int firstIndex, int baseVertex, int baseInstance) {
        dest.putInt(count);
        dest.putInt(instanceCount);
        dest.putInt(firstIndex);
        dest.putInt(baseVertex);
        dest.putInt(baseInstance);
        return 20;
    }
}
