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

package de.nekosarekawaii.vandalism.util.render.gl.vertex;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.function.BiFunction;

public class VertexLayouts {

    private static final Long2ObjectMap<VertexLayout> LAYOUT_CACHE = new Long2ObjectOpenHashMap<>();

    public static final VertexLayout POSITION = position(0, 0);
    public static final VertexLayout POSITION_COLOR8 = positionColor8(0, 0);
    public static final VertexLayout POSITION_COLORF = positionColorF(0, 0);
    public static final VertexLayout POSITION_TEX = positionTex(0, 0);
    public static final VertexLayout POSITION_NORMAL = positionNormal(0, 0);
    public static final VertexLayout POSITION_TEX_COLOR8 = positionTexColor8(0, 0);
    public static final VertexLayout POSITION_TEX_COLORF = positionTexColorF(0, 0);
    public static final VertexLayout POSITION_COLOR8_NORMAL = positionColor8Normal(0, 0);
    public static final VertexLayout POSITION_COLORF_NORMAL = positionColorFNormal(0, 0);
    public static final VertexLayout POSITION_TEX_COLOR8_NORMAL = positionTexColor8Normal(0, 0);
    public static final VertexLayout POSITION_TEX_COLORF_NORMAL = positionTexColorFNormal(0, 0);
    public static final VertexLayout SDF_TEXT = sdfText(0, 0);

    private static VertexLayout cache(int cacheId, int baseAttribIndex, int divisor, BiFunction<Integer, Integer, VertexLayout> layoutSupplier) {
        // Bits 0-7: Cache ID, Bits 8-39: Divisor, Bits 40-63: Base attrib index
        final long key = (long) (cacheId & 0xFF) | (((long) divisor & 0xFFFFFFFFL) << 8) | ((long) (baseAttribIndex & 0xFFFFFF) << 40);
        VertexLayout layout = LAYOUT_CACHE.get(key);
        if (layout == null) {
            layout = layoutSupplier.apply(baseAttribIndex, divisor);
            LAYOUT_CACHE.put(key, layout);
        }
        return layout;
    }

    public static VertexLayout position(int baseAttribIndex, int divisor) {
        return cache(0, baseAttribIndex, divisor, (attrib, div) -> VertexLayout.createPacketLayout(div, positionElement(attrib)));
    }

    public static VertexLayout positionColor8(int baseAttribIndex, int divisor) {
        return cache(1, baseAttribIndex, divisor, (attrib, div) -> VertexLayout.createPacketLayout(div, positionElement(attrib), color8Element(attrib + 1)));
    }

    public static VertexLayout positionColorF(int baseAttribIndex, int divisor) {
        return cache(2, baseAttribIndex, divisor, (attrib, div) -> VertexLayout.createPacketLayout(div, positionElement(attrib), colorFElement(attrib + 1)));
    }

    public static VertexLayout positionTex(int baseAttribIndex, int divisor) {
        return cache(3, baseAttribIndex, divisor, (attrib, div) -> VertexLayout.createPacketLayout(div, positionElement(attrib), texCoordElement(attrib + 1)));
    }

    public static VertexLayout positionNormal(int baseAttribIndex, int divisor) {
        return cache(4, baseAttribIndex, divisor, (attrib, div) -> VertexLayout.createPacketLayout(div, positionElement(attrib), normalElement(attrib + 1)));
    }

    public static VertexLayout positionTexColor8(int baseAttribIndex, int divisor) {
        return cache(5, baseAttribIndex, divisor, (attrib, div) -> VertexLayout.createPacketLayout(div, positionElement(attrib), texCoordElement(attrib + 1), color8Element(attrib + 2)));
    }

    public static VertexLayout positionTexColorF(int baseAttribIndex, int divisor) {
        return cache(6, baseAttribIndex, divisor, (attrib, div) -> VertexLayout.createPacketLayout(div, positionElement(attrib), texCoordElement(attrib + 1), colorFElement(attrib + 2)));
    }

    public static VertexLayout positionColor8Normal(int baseAttribIndex, int divisor) {
        return cache(7, baseAttribIndex, divisor, (attrib, div) -> VertexLayout.createPacketLayout(div, positionElement(attrib), color8Element(attrib + 1), normalElement(attrib + 2)));
    }

    public static VertexLayout positionColorFNormal(int baseAttribIndex, int divisor) {
        return cache(8, baseAttribIndex, divisor, (attrib, div) -> VertexLayout.createPacketLayout(div, positionElement(attrib), colorFElement(attrib + 1), normalElement(attrib + 2)));
    }

    public static VertexLayout positionTexColor8Normal(int baseAttribIndex, int divisor) {
        return cache(9, baseAttribIndex, divisor, (attrib, div) -> VertexLayout.createPacketLayout(div, positionElement(attrib), texCoordElement(attrib + 1), color8Element(attrib + 2), normalElement(attrib + 3)));
    }

    public static VertexLayout positionTexColorFNormal(int baseAttribIndex, int divisor) {
        return cache(10, baseAttribIndex, divisor, (attrib, div) -> VertexLayout.createPacketLayout(div, positionElement(attrib), texCoordElement(attrib + 1), colorFElement(attrib + 2), normalElement(attrib + 3)));
    }

    public static VertexLayout sdfText(int baseAttribIndex, int divisor) {
        return cache(11, baseAttribIndex, divisor, (attrib, div) -> VertexLayout.createPacketLayout(
                div,
                positionElement(attrib),
                texCoordElement(attrib + 1),
                color8Element(attrib + 2),
                VertexLayoutElement.builder(attrib + 3, DataType.FLOAT),
                VertexLayoutElement.builder(attrib + 4, DataType.UNSIGNED_BYTE)
        ));
    }

    public static VertexLayout halfPosition(int baseAttribIndex, int divisor) {
        return cache(12, baseAttribIndex, divisor, (attrib, div) -> VertexLayout.createPacketLayout(div, halfPositionElement(attrib)));
    }

    public static VertexLayout halfPositionColor8(int baseAttribIndex, int divisor) {
        return cache(13, baseAttribIndex, divisor, (attrib, div) -> VertexLayout.createPacketLayout(div, halfPositionElement(attrib), color8Element(attrib + 1)));
    }

    public static VertexLayoutElement.Builder positionElement(int attribIndex) {
        return VertexLayoutElement.builder(attribIndex, DataType.FLOAT).count(3);
    }

    public static VertexLayoutElement.Builder color8Element(int attribIndex) {
        return VertexLayoutElement.builder(attribIndex, DataType.UNSIGNED_BYTE).count(4).integer(false).normalized(true);
    }

    public static VertexLayoutElement.Builder colorFElement(int attribIndex) {
        return VertexLayoutElement.builder(attribIndex, DataType.FLOAT).count(4);
    }

    public static VertexLayoutElement.Builder texCoordElement(int attribIndex) {
        return VertexLayoutElement.builder(attribIndex, DataType.FLOAT).count(2);
    }

    public static VertexLayoutElement.Builder normalElement(int attribIndex) {
        return VertexLayoutElement.builder(attribIndex, DataType.BYTE).integer(false).normalized(true).count(3);
    }

    public static VertexLayoutElement.Builder halfPositionElement(int attribIndex) {
        return VertexLayoutElement.builder(attribIndex, DataType.HALF_FLOAT).count(3);
    }
}
