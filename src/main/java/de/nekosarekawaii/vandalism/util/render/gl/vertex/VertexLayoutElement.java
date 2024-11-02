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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class VertexLayoutElement {

    private final int attribIndex;
    private final DataType dataType;
    private final int count;
    @Getter
    private final boolean normalized;
    @Getter
    private final boolean integer;
    private final int offset;

    public int attribIndex() {
        return this.attribIndex;
    }

    public DataType dataType() {
        return this.dataType;
    }

    public int count() {
        return this.count;
    }

    public int offset() {
        return this.offset;
    }

    public int byteSize() {
        return this.dataType.byteSize() * this.count;
    }

    public Builder toBuilder() {
        return new Builder(this.attribIndex, this.dataType)
                .count(this.count)
                .normalized(this.normalized)
                .integer(this.integer)
                .offset(this.offset);
    }

    public static Builder createByte(int attribIndex) {
        return builder(attribIndex, DataType.BYTE);
    }

    public static Builder createUnsignedByte(int attribIndex) {
        return builder(attribIndex, DataType.UNSIGNED_BYTE);
    }

    public static Builder createShort(int attribIndex) {
        return builder(attribIndex, DataType.SHORT);
    }

    public static Builder createUnsignedShort(int attribIndex) {
        return builder(attribIndex, DataType.UNSIGNED_SHORT);
    }

    public static Builder createInt(int attribIndex) {
        return builder(attribIndex, DataType.INT);
    }

    public static Builder createUnsignedInt(int attribIndex) {
        return builder(attribIndex, DataType.UNSIGNED_INT);
    }

    public static Builder createHalfFloat(int attribIndex) {
        return builder(attribIndex, DataType.HALF_FLOAT);
    }

    public static Builder createFloat(int attribIndex) {
        return builder(attribIndex, DataType.FLOAT);
    }

    public static Builder createDouble(int attribIndex) {
        return builder(attribIndex, DataType.DOUBLE);
    }

    public static Builder createFixed(int attribIndex) {
        return builder(attribIndex, DataType.FIXED);
    }

    public static Builder builder(int attribIndex, DataType dataType) {
        return new Builder(attribIndex, dataType);
    }

    @Getter
    public static class Builder {
        private int attribIndex;
        private DataType dataType;
        private int count;
        private boolean normalized;
        private boolean integer;
        private int offset;
        @Getter(AccessLevel.NONE)
        private boolean offsetSet;

        public Builder(int attribIndex, DataType dataType) {
            this.attribIndex = attribIndex;
            this.dataType = dataType;
            this.count = 1;
            this.integer = dataType.isInteger();
            this.normalized = false;
            this.offset = 0;
            this.offsetSet = false;
        }

        public Builder attribIndex(int attribIndex) {
            this.attribIndex = attribIndex;
            return this;
        }

        public Builder dataType(DataType dataType) {
            this.dataType = dataType;
            return this;
        }

        public Builder count(int count) {
            this.count = count;
            return this;
        }

        public Builder normalized(boolean normalized) {
            this.normalized = normalized;
            return this;
        }

        public Builder integer(boolean integer) {
            this.integer = integer;
            return this;
        }

        public Builder offset(int offset) {
            this.offset = offset;
            this.offsetSet = true;
            return this;
        }

        public boolean isOffsetSet() {
            return offsetSet || this.offset != 0;
        }

        public VertexLayoutElement build() {
            return new VertexLayoutElement(this.attribIndex, this.dataType, this.count, this.normalized, this.integer, this.offset);
        }
    }
}
