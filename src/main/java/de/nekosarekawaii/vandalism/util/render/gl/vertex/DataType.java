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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL45C;

import java.nio.ByteBuffer;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum DataType {

    BYTE("byte", 1, GL45C.GL_BYTE, false, false, ByteBuffer::get, (buf, val) -> buf.put(val.byteValue())),
    UNSIGNED_BYTE("unsigned_byte", 1, GL45C.GL_UNSIGNED_BYTE, false, false, ByteBuffer::get, (buf, val) -> buf.put(val.byteValue())),
    SHORT("short", 2, GL45C.GL_SHORT, false, false, ByteBuffer::getShort, (buf, val) -> buf.putShort(val.shortValue())),
    UNSIGNED_SHORT("unsigned_short", 2, GL45C.GL_UNSIGNED_SHORT, false, false, buf -> buf.getShort() & 0xFFFF, (buf, val) -> buf.putShort(val.shortValue())),
    INT("int", 4, GL45C.GL_INT, false, false, ByteBuffer::getInt, (buf, val) -> buf.putInt(val.intValue())),
    UNSIGNED_INT("unsigned_int", 4, GL45C.GL_UNSIGNED_INT, false, false, ByteBuffer::getInt, (buf, val) -> buf.putInt(val.intValue())),
    FIXED("fixed", 4, GL45C.GL_FIXED, true, false, buf -> buf.getInt() / 65536.0f, (buf, val) -> buf.putInt((int) (val.floatValue() * 65536.0f))),
    HALF_FLOAT("half_float", 2, GL45C.GL_HALF_FLOAT, true, false, buf -> halfBitsToFloat(buf.getShort() & 0xFFFF), (buf, val) -> buf.putShort((short) (floatToHalfBits(val.floatValue()) & 0xFFFF))),
    FLOAT("float", 4, GL45C.GL_FLOAT, true, false, ByteBuffer::getFloat, (buf, val) -> buf.putFloat(val.floatValue())),
    DOUBLE("double", 8, GL45C.GL_DOUBLE, true, true, ByteBuffer::getDouble, (buf, val) -> buf.putDouble(val.doubleValue()));

    private final String name;
    @Getter(AccessLevel.NONE)
    private final int byteSize;
    private final int glType;
    private final boolean decimal;
    private final boolean doublePrecision;
    @Getter(AccessLevel.NONE)
    private final Function<ByteBuffer, Number> readFunction;
    @Getter(AccessLevel.NONE)
    private final BiConsumer<ByteBuffer, Number> writeFunction;

    public int byteSize() {
        return this.byteSize;
    }

    public boolean isInteger() {
        return !this.decimal;
    }

    public <T extends Number> T read(ByteBuffer buffer) {
        return (T) this.readFunction.apply(buffer);
    }

    public void write(ByteBuffer buffer, Number value) {
        this.writeFunction.accept(buffer, value);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static DataType byGlType(int glType) {
        for (DataType dataType : values()) {
            if (dataType.glType == glType) {
                return dataType;
            }
        }
        return null;
    }

    // Conversion methods from: https://stackoverflow.com/questions/6162651/half-precision-floating-point-in-java

    public static float halfBitsToFloat(int hbits) {
        int mant = hbits & 0x03ff;            // 10 bits mantissa
        int exp =  hbits & 0x7c00;            // 5 bits exponent
        if( exp == 0x7c00 ) {                 // NaN/Inf
            exp = 0x3fc00;                    // -> NaN/Inf
        } else if (exp != 0) {                // normalized value
            exp += 0x1c000;                   // exp - 15 + 127
            if( mant == 0 && exp > 0x1c400 )  // smooth transition
                return Float.intBitsToFloat( ( hbits & 0x8000 ) << 16
                        | exp << 13 | 0x3ff );
        } else if (mant != 0) {               // && exp==0 -> subnormal
            exp = 0x1c400;                    // make it normal
            do {
                mant <<= 1;                   // mantissa * 2
                exp -= 0x400;                 // decrease exp by 1
            } while((mant & 0x400) == 0);     // while not normal
            mant &= 0x3ff;                    // discard subnormal bit
        }                                     // else +/-0 -> +/-0
        return Float.intBitsToFloat(          // combine all parts
                (hbits & 0x8000) << 16        // sign  << ( 31 - 15 )
                    | (exp | mant) << 13 );   // value << ( 23 - 10 )
    }

    public static int floatToHalfBits(float fval) {
        int fbits = Float.floatToIntBits( fval );
        int sign = fbits >>> 16 & 0x8000;          // sign only
        int val = ( fbits & 0x7fffffff ) + 0x1000; // rounded value

        if (val >= 0x47800000) {              // might be or become NaN/Inf, avoid Inf due to rounding
            if ((fbits & 0x7fffffff) >= 0x47800000) { // is or must become NaN/Inf
                if(val < 0x7f800000)        // was value but too large
                    return sign | 0x7c00;     // make it +/-Inf
                return sign | 0x7c00 |        // remains +/-Inf or NaN
                        ( fbits & 0x007fffff ) >>> 13; // keep NaN (and Inf) bits
            }
            return sign | 0x7bff;             // unrounded not quite Inf
        }
        if (val >= 0x38800000)                // remains normalized value
            return sign | val - 0x38000000 >>> 13; // exp - 127 + 15
        if (val < 0x33000000)                 // too small for subnormal
            return sign;                      // becomes +/-0
        val = (fbits & 0x7fffffff) >>> 23;    // tmp exp for subnormal calc
        return sign | ((fbits & 0x7fffff | 0x800000) // add subnormal bit
                + ( 0x800000 >>> val - 102 )     // round depending on cut off
                >>> 126 - val);   // div by 2^(1-(exp-127+15)) and >> 13 | exp=0
    }
}
