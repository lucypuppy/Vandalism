package re.catgirls.packets.buffer;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.util.ByteProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class PacketBuffer extends ByteBuf {

    private final ByteBuf internalBuffer;

    /**
     * Creates a new packet buffer
     *
     * @param internalBuffer the internal buffer
     */
    public PacketBuffer(final ByteBuf internalBuffer) {
        this.internalBuffer = internalBuffer;
    }

    /**
     * Creates a new packet buffer
     */
    public PacketBuffer() {
        this(Unpooled.buffer(2048));
    }

    /**
     * Writes a string to the buffer
     *
     * @param string the string to write
     */
    public void writeString(final String string) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        writeInt(bytes.length);
        writeBytes(bytes);
    }

    /**
     * Reads a string from the buffer
     *
     * @return the string
     */
    public String readString() {
        byte[] bytes = new byte[readInt()];
        readBytes(bytes);

        return new String(bytes, StandardCharsets.UTF_8);
    }


    /**
     * Writes a json object to the buffer
     *
     * @param object the object to write
     */
    public void writeJson(final JsonObject object) {
        writeString(new GsonBuilder().create().toJson(object));
    }

    /**
     * Reads a json object from the buffer
     *
     * @return the json object
     */
    public JsonObject readJson() {
        return JsonParser.parseString(readString()).getAsJsonObject();
    }

    @Override
    public int capacity() {
        return internalBuffer.capacity();
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        return internalBuffer.capacity(newCapacity);
    }

    @Override
    public int maxCapacity() {
        return internalBuffer.maxCapacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return internalBuffer.alloc();
    }

    @Override
    public ByteOrder order() {
        return internalBuffer.order();
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        return internalBuffer.order(endianness);
    }

    @Override
    public ByteBuf unwrap() {
        return internalBuffer.unwrap();
    }

    @Override
    public boolean isDirect() {
        return internalBuffer.isDirect();
    }

    @Override
    public boolean isReadOnly() {
        return internalBuffer.isReadOnly();
    }

    @Override
    public ByteBuf asReadOnly() {
        return internalBuffer.asReadOnly();
    }

    @Override
    public int readerIndex() {
        return internalBuffer.readerIndex();
    }

    @Override
    public ByteBuf readerIndex(int readerIndex) {
        return internalBuffer.readerIndex(readerIndex);
    }

    @Override
    public int writerIndex() {
        return internalBuffer.writerIndex();
    }

    @Override
    public ByteBuf writerIndex(int writerIndex) {
        return internalBuffer.writerIndex(writerIndex);
    }

    @Override
    public ByteBuf setIndex(int readerIndex, int writerIndex) {
        return internalBuffer.setIndex(readerIndex, writerIndex);
    }

    @Override
    public int readableBytes() {
        return internalBuffer.readableBytes();
    }

    @Override
    public int writableBytes() {
        return internalBuffer.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return internalBuffer.maxWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return internalBuffer.isReadable();
    }

    @Override
    public boolean isReadable(int size) {
        return internalBuffer.isReadable(size);
    }

    @Override
    public boolean isWritable() {
        return internalBuffer.isWritable();
    }

    @Override
    public boolean isWritable(int size) {
        return internalBuffer.isWritable(size);
    }

    @Override
    public ByteBuf clear() {
        return internalBuffer.clear();
    }

    @Override
    public ByteBuf markReaderIndex() {
        return internalBuffer.markReaderIndex();
    }

    @Override
    public ByteBuf resetReaderIndex() {
        return internalBuffer.resetReaderIndex();
    }

    @Override
    public ByteBuf markWriterIndex() {
        return internalBuffer.markWriterIndex();
    }

    @Override
    public ByteBuf resetWriterIndex() {
        return internalBuffer.resetWriterIndex();
    }

    @Override
    public ByteBuf discardReadBytes() {
        return internalBuffer.discardReadBytes();
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        return internalBuffer.discardSomeReadBytes();
    }

    @Override
    public ByteBuf ensureWritable(int minWritableBytes) {
        return internalBuffer.ensureWritable(minWritableBytes);
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return internalBuffer.ensureWritable(minWritableBytes, force);
    }

    @Override
    public boolean getBoolean(int index) {
        return internalBuffer.getBoolean(index);
    }

    @Override
    public byte getByte(int index) {
        return internalBuffer.getByte(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        return internalBuffer.getUnsignedByte(index);
    }

    @Override
    public short getShort(int index) {
        return internalBuffer.getShort(index);
    }

    @Override
    public short getShortLE(int index) {
        return internalBuffer.getShortLE(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        return internalBuffer.getUnsignedShort(index);
    }

    @Override
    public int getUnsignedShortLE(int index) {
        return internalBuffer.getUnsignedShortLE(index);
    }

    @Override
    public int getMedium(int index) {
        return internalBuffer.getMedium(index);
    }

    @Override
    public int getMediumLE(int index) {
        return internalBuffer.getMediumLE(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return internalBuffer.getUnsignedMedium(index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        return internalBuffer.getUnsignedMediumLE(index);
    }

    @Override
    public int getInt(int index) {
        return internalBuffer.getInt(index);
    }

    @Override
    public int getIntLE(int index) {
        return internalBuffer.getIntLE(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return internalBuffer.getUnsignedInt(index);
    }

    @Override
    public long getUnsignedIntLE(int index) {
        return internalBuffer.getUnsignedIntLE(index);
    }

    @Override
    public long getLong(int index) {
        return internalBuffer.getLong(index);
    }

    @Override
    public long getLongLE(int index) {
        return internalBuffer.getLongLE(index);
    }

    @Override
    public char getChar(int index) {
        return internalBuffer.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        return internalBuffer.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        return internalBuffer.getDouble(index);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst) {
        return internalBuffer.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int length) {
        return internalBuffer.getBytes(index, dst, length);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        return internalBuffer.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst) {
        return internalBuffer.getBytes(index, dst);
    }


    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        return internalBuffer.getBytes(index, dst, dstIndex, length);
    }


    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        return internalBuffer.getBytes(index, dst);
    }


    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        return internalBuffer.getBytes(index, out, length);
    }


    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return internalBuffer.getBytes(index, out, length);
    }


    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return internalBuffer.getBytes(index, out, position, length);
    }


    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return internalBuffer.getCharSequence(index, length, charset);
    }


    @Override
    public ByteBuf setBoolean(int index, boolean value) {
        return internalBuffer.setBoolean(index, value);
    }


    @Override
    public ByteBuf setByte(int index, int value) {
        return internalBuffer.setByte(index, value);
    }


    @Override
    public ByteBuf setShort(int index, int value) {
        return internalBuffer.setShort(index, value);
    }


    @Override
    public ByteBuf setShortLE(int index, int value) {
        return internalBuffer.setShortLE(index, value);
    }


    @Override
    public ByteBuf setMedium(int index, int value) {
        return internalBuffer.setMedium(index, value);
    }


    @Override
    public ByteBuf setMediumLE(int index, int value) {
        return internalBuffer.setMediumLE(index, value);
    }


    @Override
    public ByteBuf setInt(int index, int value) {
        return internalBuffer.setInt(index, value);
    }


    @Override
    public ByteBuf setIntLE(int index, int value) {
        return internalBuffer.setIntLE(index, value);
    }


    @Override
    public ByteBuf setLong(int index, long value) {
        return internalBuffer.setLong(index, value);
    }


    @Override
    public ByteBuf setLongLE(int index, long value) {
        return internalBuffer.setLongLE(index, value);
    }


    @Override
    public ByteBuf setChar(int index, int value) {
        return internalBuffer.setChar(index, value);
    }


    @Override
    public ByteBuf setFloat(int index, float value) {
        return internalBuffer.setFloat(index, value);
    }


    @Override
    public ByteBuf setDouble(int index, double value) {
        return internalBuffer.setDouble(index, value);
    }


    @Override
    public ByteBuf setBytes(int index, ByteBuf src) {
        return internalBuffer.setBytes(index, src);
    }


    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) {
        return internalBuffer.setBytes(index, src, length);
    }


    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        return internalBuffer.setBytes(index, src, srcIndex, length);
    }


    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        return internalBuffer.setBytes(index, src);
    }


    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        return internalBuffer.setBytes(index, src, srcIndex, length);
    }


    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        return internalBuffer.setBytes(index, src);
    }


    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return internalBuffer.setBytes(index, in, length);
    }


    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return internalBuffer.setBytes(index, in, length);
    }


    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        return internalBuffer.setBytes(index, in, position, length);
    }


    @Override
    public ByteBuf setZero(int index, int length) {
        return internalBuffer.setZero(index, length);
    }


    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return internalBuffer.setCharSequence(index, sequence, charset);
    }


    @Override
    public boolean readBoolean() {
        return internalBuffer.readBoolean();
    }


    @Override
    public byte readByte() {
        return internalBuffer.readByte();
    }


    @Override
    public short readUnsignedByte() {
        return internalBuffer.readUnsignedByte();
    }


    @Override
    public short readShort() {
        return internalBuffer.readShort();
    }


    @Override
    public short readShortLE() {
        return internalBuffer.readShortLE();
    }


    @Override
    public int readUnsignedShort() {
        return internalBuffer.readUnsignedShort();
    }


    @Override
    public int readUnsignedShortLE() {
        return internalBuffer.readUnsignedShortLE();
    }


    @Override
    public int readMedium() {
        return internalBuffer.readMedium();
    }


    @Override
    public int readMediumLE() {
        return internalBuffer.readMediumLE();
    }


    @Override
    public int readUnsignedMedium() {
        return internalBuffer.readUnsignedMedium();
    }


    @Override
    public int readUnsignedMediumLE() {
        return internalBuffer.readUnsignedMediumLE();
    }


    @Override
    public int readInt() {
        return internalBuffer.readInt();
    }


    @Override
    public int readIntLE() {
        return internalBuffer.readIntLE();
    }


    @Override
    public long readUnsignedInt() {
        return internalBuffer.readUnsignedInt();
    }


    @Override
    public long readUnsignedIntLE() {
        return internalBuffer.readUnsignedIntLE();
    }


    @Override
    public long readLong() {
        return internalBuffer.readLong();
    }


    @Override
    public long readLongLE() {
        return internalBuffer.readLongLE();
    }


    @Override
    public char readChar() {
        return internalBuffer.readChar();
    }


    @Override
    public float readFloat() {
        return internalBuffer.readFloat();
    }


    @Override
    public double readDouble() {
        return internalBuffer.readDouble();
    }


    @Override
    public ByteBuf readBytes(int length) {
        return internalBuffer.readBytes(length);
    }


    @Override
    public ByteBuf readSlice(int length) {
        return internalBuffer.readSlice(length);
    }


    @Override
    public ByteBuf readRetainedSlice(int length) {
        return internalBuffer.readRetainedSlice(length);
    }


    @Override
    public ByteBuf readBytes(ByteBuf dst) {
        return internalBuffer.readBytes(dst);
    }


    @Override
    public ByteBuf readBytes(ByteBuf dst, int length) {
        return internalBuffer.readBytes(dst, length);
    }


    @Override
    public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        return internalBuffer.readBytes(dst, dstIndex, length);
    }


    @Override
    public ByteBuf readBytes(byte[] dst) {
        return internalBuffer.readBytes(dst);
    }


    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        return internalBuffer.readBytes(dst, dstIndex, length);
    }


    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        return internalBuffer.readBytes(dst);
    }


    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        return internalBuffer.readBytes(out, length);
    }


    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        return internalBuffer.readBytes(out, length);
    }


    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        return internalBuffer.readCharSequence(length, charset);
    }


    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        return internalBuffer.readBytes(out, position, length);
    }


    @Override
    public ByteBuf skipBytes(int length) {
        return internalBuffer.skipBytes(length);
    }


    @Override
    public ByteBuf writeBoolean(boolean value) {
        return internalBuffer.writeBoolean(value);
    }


    @Override
    public ByteBuf writeByte(int value) {
        return internalBuffer.writeByte(value);
    }


    @Override
    public ByteBuf writeShort(int value) {
        return internalBuffer.writeShort(value);
    }


    @Override
    public ByteBuf writeShortLE(int value) {
        return internalBuffer.writeShortLE(value);
    }


    @Override
    public ByteBuf writeMedium(int value) {
        return internalBuffer.writeMedium(value);
    }


    @Override
    public ByteBuf writeMediumLE(int value) {
        return internalBuffer.writeMediumLE(value);
    }


    @Override
    public ByteBuf writeInt(int value) {
        return internalBuffer.writeInt(value);
    }


    @Override
    public ByteBuf writeIntLE(int value) {
        return internalBuffer.writeIntLE(value);
    }


    @Override
    public ByteBuf writeLong(long value) {
        return internalBuffer.writeLong(value);
    }


    @Override
    public ByteBuf writeLongLE(long value) {
        return internalBuffer.writeLongLE(value);
    }


    @Override
    public ByteBuf writeChar(int value) {
        return internalBuffer.writeChar(value);
    }


    @Override
    public ByteBuf writeFloat(float value) {
        return internalBuffer.writeFloat(value);
    }


    @Override
    public ByteBuf writeDouble(double value) {
        return internalBuffer.writeDouble(value);
    }


    @Override
    public ByteBuf writeBytes(ByteBuf src) {
        return internalBuffer.writeBytes(src);
    }


    @Override
    public ByteBuf writeBytes(ByteBuf src, int length) {
        return internalBuffer.writeBytes(src, length);
    }


    @Override
    public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        return internalBuffer.writeBytes(src, srcIndex, length);
    }


    @Override
    public ByteBuf writeBytes(byte[] src) {
        return internalBuffer.writeBytes(src);
    }


    @Override
    public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        return internalBuffer.writeBytes(src, srcIndex, length);
    }


    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        return internalBuffer.writeBytes(src);
    }


    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        return internalBuffer.writeBytes(in, length);
    }


    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        return internalBuffer.writeBytes(in, length);
    }


    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        return internalBuffer.writeBytes(in, position, length);
    }


    @Override
    public ByteBuf writeZero(int length) {
        return internalBuffer.writeZero(length);
    }


    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        return internalBuffer.writeCharSequence(sequence, charset);
    }


    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return internalBuffer.indexOf(fromIndex, toIndex, value);
    }


    @Override
    public int bytesBefore(byte value) {
        return internalBuffer.bytesBefore(value);
    }


    @Override
    public int bytesBefore(int length, byte value) {
        return internalBuffer.bytesBefore(length, value);
    }


    @Override
    public int bytesBefore(int index, int length, byte value) {
        return internalBuffer.bytesBefore(index, length, value);
    }


    @Override
    public int forEachByte(ByteProcessor processor) {
        return internalBuffer.forEachByte(processor);
    }


    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        return internalBuffer.forEachByte(index, length, processor);
    }


    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        return internalBuffer.forEachByteDesc(processor);
    }


    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return internalBuffer.forEachByteDesc(index, length, processor);
    }


    @Override
    public ByteBuf copy() {
        return internalBuffer.copy();
    }


    @Override
    public ByteBuf copy(int index, int length) {
        return internalBuffer.copy(index, length);
    }


    @Override
    public ByteBuf slice() {
        return internalBuffer.slice();
    }


    @Override
    public ByteBuf retainedSlice() {
        return internalBuffer.retainedSlice();
    }


    @Override
    public ByteBuf slice(int index, int length) {
        return internalBuffer.slice(index, length);
    }


    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return internalBuffer.retainedSlice(index, length);
    }


    @Override
    public ByteBuf duplicate() {
        return internalBuffer.duplicate();
    }


    @Override
    public ByteBuf retainedDuplicate() {
        return internalBuffer.retainedDuplicate();
    }


    @Override
    public int nioBufferCount() {
        return internalBuffer.nioBufferCount();
    }


    @Override
    public ByteBuffer nioBuffer() {
        return internalBuffer.nioBuffer();
    }


    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return internalBuffer.nioBuffer(index, length);
    }


    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return internalBuffer.internalNioBuffer(index, length);
    }


    @Override
    public ByteBuffer[] nioBuffers() {
        return internalBuffer.nioBuffers();
    }


    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return internalBuffer.nioBuffers(index, length);
    }


    @Override
    public boolean hasArray() {
        return internalBuffer.hasArray();
    }


    @Override
    public byte[] array() {
        return internalBuffer.array();
    }


    @Override
    public int arrayOffset() {
        return internalBuffer.arrayOffset();
    }


    @Override
    public boolean hasMemoryAddress() {
        return internalBuffer.hasMemoryAddress();
    }


    @Override
    public long memoryAddress() {
        return internalBuffer.memoryAddress();
    }


    @Override
    public String toString(Charset charset) {
        return internalBuffer.toString(charset);
    }


    @Override
    public String toString(int index, int length, Charset charset) {
        return internalBuffer.toString(index, length, charset);
    }


    public int hashCode() {
        return internalBuffer.hashCode();
    }


    public boolean equals(Object obj) {
        return (obj.getClass().equals(PacketBuffer.class)) && internalBuffer.equals(obj);
    }


    @Override
    public int compareTo(ByteBuf buffer) {
        return internalBuffer.compareTo(buffer);
    }


    public String toString() {
        return internalBuffer.toString();
    }

    @Override
    public ByteBuf retain(int increment) {
        return internalBuffer.retain(increment);
    }

    @Override
    public int refCnt() {
        return internalBuffer.refCnt();
    }

    @Override
    public ByteBuf retain() {
        return internalBuffer.retain();
    }

    @Override
    public ByteBuf touch() {
        return internalBuffer.touch();
    }

    @Override
    public ByteBuf touch(Object hint) {
        return internalBuffer.touch(hint);
    }

    @Override
    public boolean release() {
        return internalBuffer.release();
    }

    @Override
    public boolean release(int i) {
        return internalBuffer.release(i);
    }
}
