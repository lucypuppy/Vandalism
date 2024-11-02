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

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * Represents the layout of data for a single buffer object.
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class VertexLayout {

    private final VertexLayoutElement[] elements;
    private final int stride;
    private final int divisor;

    /** Creates a new vertex layout where every element is tightly packed together when all offsets aren't set. Otherwise, a layout is created with the given offsets. */
    public static VertexLayout createPacketLayout(int divisor, VertexLayoutElement.Builder... elements) {
        final VertexLayoutElement[] elementArray = new VertexLayoutElement[elements.length];
        int offset = 0;
        int stride = 0;
        boolean initializeOffsets = false; // Initial value doesn't matter as it will be overwritten in the first iteration
        for (int i = 0; i < elements.length; i++) {
            if (i == 0) {
                initializeOffsets = !elements[i].isOffsetSet();
            }
            final int byteSize = elements[i].getDataType().byteSize() * elements[i].getCount();
            if (elements[i].isOffsetSet()) {
                if (initializeOffsets) throw new IllegalArgumentException("Found mixed vertex layout element values! Either set all offsets or none.");
            } else {
                if (!initializeOffsets) throw new IllegalArgumentException("Found mixed vertex layout element values! Either set all offsets or none.");
                elements[i].offset(offset);
                offset += byteSize;
            }
            elementArray[i] = elements[i].build();
            final int elementOffsetAndSize = elementArray[i].offset() + elementArray[i].byteSize();
            if (elementOffsetAndSize > stride) stride = elementOffsetAndSize;
        }
        return new VertexLayout(elementArray, stride, divisor);
    }

    public static VertexLayout create(int divisor, VertexLayoutElement... elements) {
        int stride = 0;
        for (VertexLayoutElement element : elements) {
            final int elementOffsetAndSize = element.offset() + element.byteSize();
            if (elementOffsetAndSize > stride) stride = elementOffsetAndSize;
        }
        return new VertexLayout(elements, stride, divisor);
    }

    public static VertexLayout create(int divisor, int stride, VertexLayoutElement... elements) {
        return new VertexLayout(elements, stride, divisor);
    }

    public List<VertexLayoutElement> elements() {
        return List.of(this.elements);
    }

    public int stride() {
        return this.stride;
    }

    public int divisor() {
        return this.divisor;
    }

    public VertexLayout withDivisor(int divisor) {
        return new VertexLayout(this.elements, this.stride, divisor);
    }
}
