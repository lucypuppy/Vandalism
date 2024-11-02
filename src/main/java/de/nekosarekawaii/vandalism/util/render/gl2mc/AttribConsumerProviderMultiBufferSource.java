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

package de.nekosarekawaii.vandalism.util.render.gl2mc;

import com.mojang.blaze3d.systems.RenderSystem;
import de.nekosarekawaii.vandalism.util.render.gl.render.AttribConsumer;
import de.nekosarekawaii.vandalism.util.render.gl.render.AttribConsumerProvider;
import de.nekosarekawaii.vandalism.util.render.gl.render.PrimitiveType;
import de.nekosarekawaii.vandalism.util.render.gl.render.passes.RenderPass;
import de.nekosarekawaii.vandalism.util.render.gl.shader.ShaderProgram;
import de.nekosarekawaii.vandalism.util.render.gl.vertex.DataType;
import de.nekosarekawaii.vandalism.util.render.gl.vertex.VertexLayout;
import de.nekosarekawaii.vandalism.util.render.gl.vertex.VertexLayoutElement;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.lenni0451.reflect.JavaBypass;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@RequiredArgsConstructor
public class AttribConsumerProviderMultiBufferSource implements VertexConsumerProvider {

    @Getter
    private final AttribConsumerProvider attribConsumerProvider;
    private final Object2ObjectMap<RenderLayer, RenderPass> renderTypeToRenderPass = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectMap<RenderLayer, AttribConsumerToVertexConsumer> renderTypeToPassAndConsumer = new Object2ObjectOpenHashMap<>();

    @NotNull
    @Override
    public VertexConsumer getBuffer(RenderLayer renderType) {
        RenderPass pass = this.renderTypeToRenderPass.get(renderType);
        if (pass == null) {
            pass = this.renderTypeToRenderPass(renderType);
            this.renderTypeToRenderPass.put(renderType, pass);
        }
        AttribConsumerToVertexConsumer consumer = this.renderTypeToPassAndConsumer.get(renderType);
        if (consumer == null) {
            consumer = this.createPassAndConsumer(pass);
            this.renderTypeToPassAndConsumer.put(renderType, consumer);
        } else if (renderType.areVerticesNotShared()) {
            consumer.attribConsumer.nextConnectedPrimitive();
        }
        consumer.begin(renderType.getVertexFormat());
        return consumer;
    }

    @NotNull
    public AttribConsumer getAttribConsumer(RenderLayer renderType) {
        return ((AttribConsumerToVertexConsumer) this.getBuffer(renderType)).attribConsumer;
    }

    @Nullable
    public RenderPass getRenderPass(RenderLayer renderType) {
        return this.renderTypeToRenderPass.get(renderType);
    }

    public void reset() {
        this.renderTypeToPassAndConsumer.clear();
    }

    private AttribConsumerToVertexConsumer createPassAndConsumer(RenderPass pass) {
        return new AttribConsumerToVertexConsumer(this.attribConsumerProvider.getAttribConsumers(pass).main());
    }

    private RenderPass renderTypeToRenderPass(RenderLayer renderType) {
        return new RenderPassToRenderType(renderType);
    }

    @RequiredArgsConstructor
    public static class AttribConsumerToVertexConsumer implements VertexConsumer {

        private final AttribConsumer attribConsumer;
        private int paddingBytes;
        private boolean insertPaddingAtVertexEnd;
        private VertexFormat currentFormat;
        private int currentElement;
        private float x, y, z;
        private int r, g, b, a;
        private float u, v;
        private int overlayU, overlayV;
        private int lightU, lightV;
        private float normalX, normalY, normalZ;

        public void begin(VertexFormat format) {
            this.currentFormat = format;
            this.currentElement = 0;
            //final List<VertexFormatElement> elements = format.getElements();
            /*this.insertPaddingAtVertexEnd = false;
            for (int i = 0; i < format.getElements().size(); i++) {
                final VertexFormatElement element = format.getElements().get(i);
                if (element.getUsage() == VertexFormatElement.Usage.PADDING) {
                    if (i == format.getElements().size() - 1) {
                        this.insertPaddingAtVertexEnd = true;
                        this.paddingBytes = element.getByteSize();
                        break;
                    } else {
                        throw new IllegalStateException("Padding element is not at the end of the vertex format! This not supported!");
                    }
                }
            }*/
        }

        @Override
        public VertexConsumer vertex(float x, float y, float z) {
            this.attribConsumer.next();
            this.attribConsumer.pos(x, y, z);
            this.checkForEndVertex(VertexFormatElement.Usage.POSITION);
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            this.attribConsumer.putColor8(red, green, blue, alpha);
            this.checkForEndVertex(VertexFormatElement.Usage.COLOR);
            return this;
        }

        @Override
        public VertexConsumer texture(float u, float v) {
            this.attribConsumer.putUV(u, v);
            this.checkForEndVertex(VertexFormatElement.Usage.UV);
            return this;
        }

        @Override
        public VertexConsumer overlay(int u, int v) {
            this.attribConsumer.putShort((short) (u & 0xFFFF)).putShort((short) (v & 0xFFFF));
            this.checkForEndVertex(VertexFormatElement.Usage.UV);
            return this;
        }

        @Override
        public VertexConsumer light(int u, int v) {
            this.attribConsumer.putShort((short) (u & 0xFFFF)).putShort((short) (v & 0xFFFF));
            this.checkForEndVertex(VertexFormatElement.Usage.UV);
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            this.attribConsumer.putByte(BufferBuilder.floatToByte(x))
                    .putByte(BufferBuilder.floatToByte(y))
                    .putByte(BufferBuilder.floatToByte(z));
            this.checkForEndVertex(VertexFormatElement.Usage.NORMAL);
            return this;
        }

        private void checkForEndVertex(VertexFormatElement.Usage usage) {
            /*if (this.currentElement == this.currentFormat.getElements().size()) {
                throw new IllegalStateException("Trying to add more elements to a vertex than the vertex format has!");
            }
            final VertexFormatElement element = this.currentFormat.getElements().get(this.currentElement);
            if (element.usage() != usage) {
                throw new IllegalStateException("Vertex format element at index " + this.currentElement + " is not a " + usage + " element but an " + element.usage() + " element!");
            }
            ++this.currentElement;
            if (this.currentElement >= this.currentFormat.getElements().size()) {
                this.endVertex();
            }*/
        }

        // THIS GOT REMOVED BY MINECRAFT AND NOW WE ARE EMULATING IT OURSELVES
        private void endVertex() {
            if (this.insertPaddingAtVertexEnd) {
                for (int i = 0; i < this.paddingBytes; i++) this.attribConsumer.putByte((byte) 0);
            }
            this.attribConsumer.next();
            this.currentElement = 0;
        }
    }

    @Log4j2
    private static class RenderPassToRenderType implements RenderPass {

        private static ShaderVertexBuffer cursedBuffer;
        private final RenderLayer renderType;
        private net.minecraft.client.gl.ShaderProgram lastShader;
        private net.minecraft.client.gl.ShaderProgram usedShader;
        private VertexFormat lastMainFormat;
        private VertexLayout cachedMainLayout;

        public RenderPassToRenderType(RenderLayer renderType) {
            this.renderType = renderType;
            if (cursedBuffer == null) {
                cursedBuffer = ShaderVertexBuffer.create();
            }
        }

        @Override
        public void setupUniforms(de.nekosarekawaii.vandalism.util.render.gl.shader.ShaderProgram program, int instanceCount) {
            /*if (this.lastShader == null || program.id() != this.lastShader.getId()) {
                log.warn("Shader changed during render pass, this is not supported!");
                return;
            }*/

            try {
                cursedBuffer.draw(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), this.lastShader);
            } catch (QuietException ignored) {} // QuietException is thrown by ShaderVertexBuffer.draw() to cancel the draw call

            this.usedShader = lastShader;
        }

        @Override
        public void setupState() {
            this.renderType.startDrawing();
        }

        @Override
        public void cleanupState() {
            if (this.usedShader != null) {
                this.usedShader.unbind();
                this.usedShader = null;
            }
            this.renderType.endDrawing();
        }

        @Override
        public ShaderProgram getShaderProgram() {
            this.lastShader = RenderSystem.getShader();
            return this.lastShader == null ? null : de.nekosarekawaii.vandalism.util.render.gl.shader.ShaderProgram.byId(this.lastShader.getGlRef());
        }

        @Override
        public @NotNull VertexLayout getVertexLayout() {
            final VertexFormat format = this.renderType.getVertexFormat();
            if (this.cachedMainLayout == null || this.lastMainFormat != format) {
                this.cachedMainLayout = this.vertexFormatToVertexLayout(format);
                this.lastMainFormat = format;
            }
            return this.cachedMainLayout;
        }

        private VertexLayout vertexFormatToVertexLayout(VertexFormat format) {
            final List<VertexFormatElement> elements = format.getElements();
            /*int nonPaddingElements = 0;
            for (VertexFormatElement element : elements) {
                if (element.getUsage() != VertexFormatElement.Usage.PADDING) ++nonPaddingElements;
            }*/
            int nonPaddingElements = elements.size();
            final VertexLayoutElement[] layoutElements = new VertexLayoutElement[nonPaddingElements];
            int offset = 0;
            int index = 0;
            for (int i = 0; i < elements.size(); i++) {
                final VertexFormatElement element = elements.get(i);
                /*if (element.getUsage() == VertexFormatElement.Usage.PADDING) {
                    offset += element.getSizeInBytes();
                    continue;
                }*/
                final DataType type = DataType.byGlType(element.type().getGlType());
                layoutElements[index++] = VertexLayoutElement.builder(i, type)
                        .count(element.count())
                        .integer(element.usage() == VertexFormatElement.Usage.UV && type != DataType.FLOAT)
                        .normalized(element.usage() == VertexFormatElement.Usage.COLOR || element.usage() == VertexFormatElement.Usage.NORMAL)
                        .offset(offset)
                        .build();
                offset += element.getSizeInBytes();
            }
            return VertexLayout.create(0, offset, layoutElements);
        }

        @Override
        public PrimitiveType getPrimitiveType() {
            return switch (this.renderType.getDrawMode()) {
                case LINES -> PrimitiveType.MINECRAFT_LINES;
                case LINE_STRIP -> PrimitiveType.MINECRAFT_LINE_STRIP;
                case DEBUG_LINES -> PrimitiveType.GL_LINES;
                case DEBUG_LINE_STRIP -> PrimitiveType.GL_LINE_STRIP;
                case TRIANGLES -> PrimitiveType.TRIANGLES;
                case TRIANGLE_STRIP -> PrimitiveType.TRIANGLE_STRIP;
                case TRIANGLE_FAN -> PrimitiveType.TRIANGLE_FAN;
                case QUADS -> PrimitiveType.QUADS;
            };
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }
    }

    private static class ShaderVertexBuffer extends VertexBuffer {

        private ShaderVertexBuffer(Usage usage) {
            super(usage);
        }

        @SneakyThrows
        public static ShaderVertexBuffer create() {
            return (ShaderVertexBuffer) JavaBypass.UNSAFE.allocateInstance(ShaderVertexBuffer.class);
        }

        @Override
        public void draw() {
            throw QuietException.INSTANCE; // This is a hack to cancel the drawElements(...) call in VertexBuffer.draw()
        }
    }
}
